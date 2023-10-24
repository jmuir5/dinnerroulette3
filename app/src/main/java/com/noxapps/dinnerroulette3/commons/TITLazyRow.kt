package com.noxapps.dinnerroulette3.commons

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.noxapps.dinnerroulette3.search.CusTitButton

@Composable
fun TITLazyRow(
    values: List<String>,
    states:MutableList<MutableState<Boolean>>,
    icons:Pair<ImageVector, ImageVector>,
    falsePadding: Dp = 0.dp
){
    if (values.size!= states.size) {
        states.clear()
        values.forEach{ _ ->
            states.add(mutableStateOf(true))
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (values.isNotEmpty()) {
            item() {
                Spacer(modifier = Modifier.size(falsePadding))
            }
            values.forEachIndexed() { index, value ->
                item() {
                    CusTitButton(
                        text = value,
                        value = states[index].value,
                        onClick = {
                            states[index].value = !states[index].value
                        },
                        iconInit = icons.first,//Icons.Filled.FavoriteBorder,
                        iconChecked = icons.second
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                }
            }
            item() {
                Spacer(modifier = Modifier.size(falsePadding))
            }
        }
    }
}