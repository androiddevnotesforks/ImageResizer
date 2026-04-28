/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.Rotate90Ccw: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Rotate90Ccw",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(520f, 880f)
            quadToRelative(-37f, 0f, -72.5f, -7.5f)
            reflectiveQuadTo(378f, 851f)
            quadToRelative(-15f, -6f, -20f, -21.5f)
            reflectiveQuadToRelative(1f, -30.5f)
            quadToRelative(6f, -15f, 21f, -21.5f)
            reflectiveQuadToRelative(30f, 0.5f)
            quadToRelative(26f, 11f, 53.5f, 16.5f)
            reflectiveQuadTo(520f, 800f)
            quadToRelative(117f, 0f, 198.5f, -81.5f)
            reflectiveQuadTo(800f, 520f)
            quadToRelative(0f, -117f, -82f, -198.5f)
            reflectiveQuadTo(519f, 240f)
            horizontalLineToRelative(-7f)
            lineToRelative(36f, 36f)
            quadToRelative(11f, 11f, 11f, 28f)
            reflectiveQuadToRelative(-11f, 28f)
            quadToRelative(-11f, 11f, -28f, 11f)
            reflectiveQuadToRelative(-28f, -11f)
            lineTo(388f, 228f)
            quadToRelative(-5f, -5f, -8f, -12.5f)
            reflectiveQuadToRelative(-3f, -15.5f)
            quadToRelative(0f, -8f, 3f, -15.5f)
            reflectiveQuadToRelative(8f, -12.5f)
            lineToRelative(104f, -104f)
            quadToRelative(11f, -11f, 28f, -11f)
            reflectiveQuadToRelative(28f, 11f)
            quadToRelative(11f, 11f, 11f, 28f)
            reflectiveQuadToRelative(-11f, 28f)
            lineToRelative(-36f, 36f)
            horizontalLineToRelative(7f)
            quadToRelative(150f, 0f, 255.5f, 105f)
            reflectiveQuadTo(880f, 520f)
            quadToRelative(0f, 150f, -105f, 255f)
            reflectiveQuadTo(520f, 880f)
            close()
            moveTo(265f, 740.5f)
            quadToRelative(-7f, -2.5f, -13f, -8.5f)
            lineTo(68f, 548f)
            quadToRelative(-6f, -6f, -8.5f, -13f)
            reflectiveQuadTo(57f, 520f)
            quadToRelative(0f, -8f, 2.5f, -15f)
            reflectiveQuadToRelative(8.5f, -13f)
            lineToRelative(184f, -184f)
            quadToRelative(6f, -6f, 13f, -8.5f)
            reflectiveQuadToRelative(15f, -2.5f)
            quadToRelative(8f, 0f, 15f, 2.5f)
            reflectiveQuadToRelative(13f, 8.5f)
            lineToRelative(184f, 184f)
            quadToRelative(6f, 6f, 8.5f, 13f)
            reflectiveQuadToRelative(2.5f, 15f)
            quadToRelative(0f, 8f, -2.5f, 15f)
            reflectiveQuadToRelative(-8.5f, 13f)
            lineTo(308f, 732f)
            quadToRelative(-6f, 6f, -13f, 8.5f)
            reflectiveQuadToRelative(-15f, 2.5f)
            quadToRelative(-8f, 0f, -15f, -2.5f)
            close()
            moveTo(280f, 646f)
            lineTo(406f, 520f)
            lineTo(280f, 394f)
            lineTo(154f, 520f)
            lineTo(280f, 646f)
            close()
            moveTo(280f, 520f)
            close()
        }
    }.build()
}
