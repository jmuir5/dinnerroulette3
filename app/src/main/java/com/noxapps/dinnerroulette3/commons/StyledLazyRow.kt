package com.noxapps.dinnerroulette3.commons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.noxapps.dinnerroulette3.ui.theme.md_theme_light_secondaryContainer

@Composable
fun StyledLazyRow(array: SnapshotStateList<String>, staticHeight:Boolean = false, falsePadding: Dp = 0.dp){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        if (array.isNotEmpty()) {
            //item() {
                Spacer(modifier = Modifier.size(falsePadding))
            //}
            array.forEach() { s ->
                //item() {
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(md_theme_light_secondaryContainer)
                        .padding(3.dp)
                        .clickable {
                            array.remove(s)
                        }) {
                        Row() {
                            Text(s)
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Delete",
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(2.dp))
                //}
            }
            //item() {
                Spacer(modifier = Modifier.size(falsePadding))
            //}
        }
        if (staticHeight) {
            if (array.isEmpty()) {
                //item {
                    Box(
                        modifier = Modifier
                            .padding(3.dp)
                    ) {
                        Row() {
                            Text(" ")
                        }
                    }
                //}
            }
        }

    }
}