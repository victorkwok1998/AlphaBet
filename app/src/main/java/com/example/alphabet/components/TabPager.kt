//package com.example.alphabet.components
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.material.Tab
//import androidx.compose.material.TabRow
//import androidx.compose.material.TabRowDefaults
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Modifier
//import com.google.accompanist.pager.ExperimentalPagerApi
//import com.google.accompanist.pager.HorizontalPager
//import com.google.accompanist.pager.pagerTabIndicatorOffset
//import com.google.accompanist.pager.rememberPagerState
//import kotlinx.coroutines.launch
//
//@ExperimentalPagerApi
//@Composable
//fun TabPager(tabData: List<Pair<String, @Composable () -> Unit>>) {
//    val pagerState = rememberPagerState(pageCount = tabData.size)
//    val tabIndex = pagerState.currentPage
//    val coroutineScope = rememberCoroutineScope()
//    Column {
//        TabRow(selectedTabIndex = tabIndex,
//            indicator = { tabPositions ->
//                TabRowDefaults.Indicator(
//                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
//                )
//            }
//        ) {
//            tabData.forEachIndexed { index, pair ->
//                Tab(selected = tabIndex == index, onClick = {
//                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
//                },
//                    text = { Text(text = pair.first) })
//            }
//        }
//        HorizontalPager(state = pagerState,
////            modifier = Modifier.weight(1f)
//        ) { index ->
//            tabData[index].second()
//        }
//    }
//}