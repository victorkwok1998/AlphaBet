import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.alphabet.Cond
import com.example.alphabet.RuleInput
import com.example.alphabet.StaticDataViewModel
import com.example.alphabet.StrategyViewModel
import com.example.alphabet.components.CreateRule
import com.example.alphabet.components.MyTopAppBar
import com.example.alphabet.ui.theme.grayBackground

class CreateRuleFragment: Fragment() {
    private val viewModel: StrategyViewModel by activityViewModels()
    private val staticDataViewModel: StaticDataViewModel by activityViewModels()

    @ExperimentalComposeUiApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val selectedRule = viewModel.selectedRule.value
        val primaryParamNames = staticDataViewModel.indToParamList.value[selectedRule.indInput1.indName]?: listOf()
        val secondaryParamNames = staticDataViewModel.indToParamList.value[selectedRule.indInput2.indName]?: listOf()

        return ComposeView(requireContext()).apply {
            setContent {
                CreateRuleScreen(selectedRule, primaryParamNames, secondaryParamNames)
            }
        }
    }
    
    @ExperimentalComposeUiApi
    @Composable
    fun CreateRuleScreen(
        rule: RuleInput,
        primaryParamNames: List<String>,
        secondParamNames: List<String>
    ) {
        Scaffold(
            topBar = { 
                MyTopAppBar(
                    title = { Text("Create Rule") },
                    navigationIcon = {
                        IconButton(onClick = { findNavController().popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                ) 
            }
        ) {
            Column(
                Modifier.background(grayBackground)
            ) {
                CreateRule(
                    primaryIndicator = rule.indInput1.indName,
                    primaryParamNames = primaryParamNames,
                    primaryParamValues = rule.indInput1.indParamList,
                    primaryOnClick = {
                        viewModel.selectedIndicator.value = viewModel.selectedRule.value.indInput1
                        val action = CreateRuleFragmentDirections.actionCreateRuleFragmentToIndicatorListFragment()
                        findNavController().navigate(action)
                    },
                    primaryOnValueChange = { index, value ->
                        rule.indInput1.indParamList[index] = value
                    },
                    secondIndicator = rule.indInput2.indName,
                    secondParamNames = secondParamNames,
                    secondParamValues = rule.indInput2.indParamList,
                    secondOnClick = {
                        viewModel.selectedIndicator.value = viewModel.selectedRule.value.indInput2
                        val action = CreateRuleFragmentDirections.actionCreateRuleFragmentToIndicatorListFragment()
                        findNavController().navigate(action)
                    },
                    secondOnValueChange = { index, value ->
                        rule.indInput2.indParamList[index] = value
                    },
                    condOnValueChange = {
                        rule.condName = Cond.fromValue(it)
                    }
                )
            }
        }
    }
}