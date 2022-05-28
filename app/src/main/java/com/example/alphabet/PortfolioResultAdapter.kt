package com.example.alphabet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alphabet.database.PortfolioResultSchema
import com.github.mikephil.charting.charts.PieChart

class PortfolioResultAdapter(
    private val context: Context,
    private val listener: OnItemClickListener,
    private val onDeleteClicked: ((PortfolioResultSchema) -> Unit)? = null,
    private val onRerunClicked: ((PortfolioResultSchema) -> Unit)? = null,
    private val onEditClicked: ((PortfolioResultSchema) -> Unit)? = null,
) :
    ListAdapter<PortfolioResultSchema, PortfolioResultAdapter.PortResultViewHolder>(PortResultDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortResultViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_portfolio_row, parent, false)

        return PortResultViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PortResultViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.portNameText.text = currentItem.name
        plotPieChart(
            context = context,
            pieChart = holder.pieChart,
            labelToVal = currentItem.portfolioInputList.associate { it.stock.symbol to it.weight.toFloat() },
            label = "",
            drawEntryLabel = false
        )
        onDeleteClicked?.apply {
            holder.deleteButton.setOnClickListener {
                this(currentItem)
            }
        }
        onEditClicked?.apply {
            holder.editButton.setOnClickListener {
                this(currentItem)
            }
        }

//        holder.moreButton.setOnClickListener {
//            BottomSheetDialog(context).apply {
//                val binding = BacktestResultRowBottomSheetBinding.inflate(LayoutInflater.from(context), null, false)
//                binding.deleteRow.setOnClickListener {
//                    onDeleteClicked(currentItem)
//                    this.dismiss()
//                }
//                binding.rerunRow.setOnClickListener {
//                    onRerunClicked(currentItem)
//                }
//                this.setContentView(binding.root)
//                this.show()
//            }
//        }

        val cashFlow = currentItem.nav
        val date = currentItem.date
        if (cashFlow.isNotEmpty()) {
            holder.timePeriodText.text = "${isoToDisplay(date.first())} - ${isoToDisplay(date.last())}"
            val pnl = cashFlow.last() / cashFlow.first() - 1
            setReturnText(context, holder.returnText, pnl) {MyApplication.pct.format(it)}

            val sr = sharpeRatio(navToReturn(cashFlow))
            setReturnText(context, holder.srText, sr) { MyApplication.dec.format(it) }
        }
    }

    inner class PortResultViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val portNameText: TextView = itemView.findViewById(R.id.text_port_name)
        val timePeriodText: TextView = itemView.findViewById(R.id.text_time_period)
        val returnText: TextView = itemView.findViewById(R.id.text_port_return)
        val pieChart: PieChart = itemView.findViewById(R.id.pie_chart_my_port)
        val srText: TextView = itemView.findViewById(R.id.text_sharpe_ratio)
        val deleteButton: Button = itemView.findViewById(R.id.button_delete)
        val editButton: Button = itemView.findViewById(R.id.button_edit_port)

        init {
            itemView.setOnClickListener(this)
            if (onDeleteClicked == null)
                deleteButton.visibility = View.GONE
            if (onEditClicked == null)
                editButton.visibility = View.GONE
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION)
                listener.onItemClick(position)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    class PortResultDiffCallback: DiffUtil.ItemCallback<PortfolioResultSchema>() {
        override fun areItemsTheSame(
            oldItem: PortfolioResultSchema,
            newItem: PortfolioResultSchema
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: PortfolioResultSchema,
            newItem: PortfolioResultSchema
        ): Boolean {
            return oldItem == newItem
        }
    }}