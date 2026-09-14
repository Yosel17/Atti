package yosel.dev.atti.screens.navigation_bar.home.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import yosel.dev.atti.R
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.FollowUpModel
import yosel.dev.atti.core.models.model.FollowUpWithDetailsModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.models.model.PatientWithDetailsModel
import yosel.dev.atti.core.models.model.ProductModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.core.utils.formatScheduledTime
import yosel.dev.atti.ui.theme.AttiTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BodyHome(
    modifier: Modifier = Modifier,
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    onNavigationMain: (Screens) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Mensaje de Bienvenida
        item {
            HeaderWelcomeSection()
        }

        // 2. Componente de Calendario con indicador de Carga / Contenido
        item {
            CalendarCard(
                state = state,
                onAction = onAction
            )
        }

        // 3. Encabezado de Citas del Día
        item {
            val isToday = state.selectedDate == LocalDate.now()
            val appointmentsTitle = if (isToday) {
                "Citas de Hoy"
            } else {
                "Citas del ${state.selectedDate.dayOfMonth}"
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = appointmentsTitle,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (state.selectedDayFollowUps.isNotEmpty()) {
                    val count = state.selectedDayFollowUps.size
                    val badgeLabel = "$count ${if (count == 1) "cita" else "citas"}"
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = badgeLabel,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // 4. Listado de Citas del Día seleccionado
        if (state.selectedDayFollowUps.isEmpty()) {
            item {
                EmptyAppointmentsCard(selectedDate = state.selectedDate)
            }
        } else {
            items(state.selectedDayFollowUps, key = { it.followUp.id }) { followUpWithDetails ->
                AppointmentItemCard(
                    modifier = Modifier.animateItem(),
                    followUpWithDetails = followUpWithDetails,
                    onClick = {
                        onNavigationMain(
                            Screens.DetailConsultation(
                                consultationId = followUpWithDetails.followUp.consultationId,
                                consultationTypeId = followUpWithDetails.consultationWithDetails.consultation.consultationTypeId
                            )
                        )
                    }
                )
            }
        }

        // 5. Encabezado de Stock Bajo
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WarningAmber,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Stock Bajo",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (state.lowStockProducts.isNotEmpty()) {
                    val criticalCount = state.lowStockProducts.size
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = "$criticalCount críticos",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // 6. Listado de Productos en Stock Bajo
        if (state.lowStockProducts.isEmpty()) {
            item {
                EmptyLowStockCard()
            }
        } else {
            items(state.lowStockProducts, key = { it.product.id }) { productWithDetails ->
                LowStockProductCard(
                    modifier = Modifier.animateItem(),
                    productWithDetails = productWithDetails,
                    onClick = {
                        onNavigationMain(
                            Screens.DetailProduct(productId = productWithDetails.product.id)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun HeaderWelcomeSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "¡Bienvenido!",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Hoy es un gran día para cuidar de las mascotas",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CalendarCard(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val esLocale = remember { Locale.forLanguageTag("es-ES") }
    val monthName = remember(state.currentYearMonth) {
        state.currentYearMonth.month.getDisplayName(TextStyle.FULL, esLocale)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(esLocale) else it.toString() }
    }
    val yearText = remember(state.currentYearMonth) { state.currentYearMonth.year.toString() }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header del calendario: Ícono, Mes/Año, Total de citas y Botones prev/next
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "$monthName $yearText",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${state.monthFollowUps.size} citas programadas este mes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { onAction(HomeAction.OnPreviousMonth) },
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                            contentDescription = "Mes anterior",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { onAction(HomeAction.OnNextMonth) },
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = "Mes siguiente",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Días de la semana (L, M, M, J, V, S, D)
            val daysOfWeek = remember { listOf("L", "M", "M", "J", "V", "S", "D") }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                daysOfWeek.forEach { dayLabel ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayLabel,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grilla de Días con transición de carga si cambia el mes
            AnimatedContent(
                targetState = state.isLoadingMonthAppointments,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "CalendarLoadingTransition"
            ) { isLoading ->
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator(modifier = Modifier.size(40.dp))
                    }
                } else {
                    CalendarGrid(
                        currentYearMonth = state.currentYearMonth,
                        selectedDate = state.selectedDate,
                        monthFollowUps = state.monthFollowUps,
                        onSelectDate = { onAction(HomeAction.OnSelectDate(it)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Leyenda: "Día con citas agendadas" y "Hoy"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_animals),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Día con citas agendadas",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = "Hoy",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    currentYearMonth: YearMonth,
    selectedDate: LocalDate,
    monthFollowUps: List<FollowUpWithDetailsModel>,
    onSelectDate: (LocalDate) -> Unit
) {
    val appointmentDatesSet = remember(monthFollowUps) {
        monthFollowUps.mapNotNull {
            parseScheduledLocalDate(it.followUp.scheduledAt)
        }.toSet()
    }

    val firstDayOfMonth = remember(currentYearMonth) { currentYearMonth.atDay(1) }
    val daysInMonth = remember(currentYearMonth) { currentYearMonth.lengthOfMonth() }
    val startDayOffset = remember(firstDayOfMonth) { firstDayOfMonth.dayOfWeek.value - 1 }

    val prevYearMonth = remember(currentYearMonth) { currentYearMonth.minusMonths(1) }
    val daysInPrevMonth = remember(prevYearMonth) { prevYearMonth.lengthOfMonth() }

    val totalCells = ((startDayOffset + daysInMonth + 6) / 7) * 7
    val today = remember { LocalDate.now() }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val rowsCount = totalCells / 7
        for (rowIndex in 0 until rowsCount) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (colIndex in 0 until 7) {
                    val cellIndex = rowIndex * 7 + colIndex
                    val isPrevMonth = cellIndex < startDayOffset
                    val isNextMonth = cellIndex >= (startDayOffset + daysInMonth)
                    val isCurrentMonth = !isPrevMonth && !isNextMonth

                    val (dayNumber, cellDate) = when {
                        isPrevMonth -> {
                            val prevDay = daysInPrevMonth - startDayOffset + cellIndex + 1
                            prevDay to prevYearMonth.atDay(prevDay)
                        }
                        isNextMonth -> {
                            val nextDay = cellIndex - (startDayOffset + daysInMonth) + 1
                            nextDay to currentYearMonth.plusMonths(1).atDay(nextDay)
                        }
                        else -> {
                            val currentDay = cellIndex - startDayOffset + 1
                            currentDay to currentYearMonth.atDay(currentDay)
                        }
                    }

                    val isSelected = isCurrentMonth && cellDate == selectedDate
                    val isToday = cellDate == today
                    val hasAppointment = isCurrentMonth && appointmentDatesSet.contains(cellDate)

                    CalendarDayCell(
                        modifier = Modifier.weight(1f),
                        dayNumber = dayNumber,
                        isCurrentMonth = isCurrentMonth,
                        isSelected = isSelected,
                        isToday = isToday,
                        hasAppointment = hasAppointment,
                        onClick = {
                            if (isCurrentMonth) onSelectDate(cellDate)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    dayNumber: Int,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    hasAppointment: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cellShape = RoundedCornerShape(14.dp)

    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        else -> androidx.compose.ui.graphics.Color.Transparent
    }

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        !isCurrentMonth -> MaterialTheme.colorScheme.outlineVariant
        isToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    val iconTint = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = modifier
            .height(52.dp)
            .padding(horizontal = 2.dp)
            .clip(cellShape)
            .background(backgroundColor)
            .clickable(enabled = isCurrentMonth, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = dayNumber.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium
            ),
            color = textColor
        )

        if (hasAppointment) {
            Icon(
                painter = painterResource(id = R.drawable.ic_animals),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .size(12.dp)
                    .padding(top = 2.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun AppointmentItemCard(
    followUpWithDetails: FollowUpWithDetailsModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val followUp = followUpWithDetails.followUp
    val patient = followUpWithDetails.patientWithDetails.patient
    val species = followUpWithDetails.patientWithDetails.species

    val rawTime = remember(followUp.scheduledAt) {
        formatScheduledTime(followUp.scheduledAt)
    }
    val (timeString, amPmString) = remember(rawTime) {
        val parts = rawTime.split(" ")
        if (parts.size >= 2) parts[0] to parts[1] else rawTime to ""
    }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Contenedor de la hora
            Box(
                modifier = Modifier
                    .size(width = 64.dp, height = 58.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeString,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (amPmString.isNotBlank()) {
                        Text(
                            text = amPmString,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Información del paciente y motivo
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = patient.name.ifBlank { "Sin nombre" },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val detailComplement = when {
                        patient.breed.isNotBlank() -> patient.breed
                        species.name.isNotBlank() -> species.name
                        else -> ""
                    }
                    if (detailComplement.isNotBlank()) {
                        Text(
                            text = " - $detailComplement",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Text(
                    text = followUp.reason.ifBlank { "Revisión médica agendada" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun LowStockProductCard(
    productWithDetails: ProductWithDetailsModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product = productWithDetails.product
    val unitLabel = productWithDetails.unitType.name.ifBlank { "unidades" }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Contenedor del icono de producto
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Medication,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Datos del producto
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.commercialName.ifBlank { "Sin nombre" },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = "CRÍTICO",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val stockFormatted = if (product.stock % 1.0 == 0.0) {
                        product.stock.toInt().toString()
                    } else {
                        "%.2f".format(Locale.US, product.stock)
                    }
                    Text(
                        text = "$stockFormatted $unitLabel restantes",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(Mínimo: ${product.minStock})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyAppointmentsCard(
    selectedDate: LocalDate,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.EventBusy,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Sin citas agendadas",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "No tienes citas registradas para este día.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EmptyLowStockCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircleOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Inventario saludable",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Todos los insumos y medicamentos se encuentran por encima del mínimo requerido.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun parseScheduledLocalDate(isoString: String): LocalDate? {
    if (isoString.isBlank()) return null
    return try {
        val sanitized = isoString.trim().replace(" ", "T")
        if (sanitized.contains("+") || sanitized.endsWith("Z") || Regex("[+-]\\d{2}(:\\d{2})?$").containsMatchIn(sanitized)) {
            OffsetDateTime.parse(sanitized).toLocalDate()
        } else {
            LocalDateTime.parse(sanitized).toLocalDate()
        }
    } catch (e: Exception) {
        null
    }
}

@PreviewLightDark
@Composable
private fun BodyHomePreview() {
    AttiTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            BodyHome(
                state = HomeState(
                    monthFollowUps = listOf(
                        FollowUpWithDetailsModel(
                            followUp = FollowUpModel(
                                id = "1",
                                consultationId = "c1",
                                patientId = "p1",
                                scheduledAt = "2026-09-14T09:00:00Z",
                                reason = "Revisión de la operación"
                            ),
                            patientWithDetails = PatientWithDetailsModel(
                                patient = PatientModel(name = "Luna", breed = "Felino")
                            )
                        )
                    ),
                    selectedDayFollowUps = listOf(
                        FollowUpWithDetailsModel(
                            followUp = FollowUpModel(
                                id = "1",
                                consultationId = "c1",
                                patientId = "p1",
                                scheduledAt = "2026-09-14T09:00:00Z",
                                reason = "Revisión de la operación"
                            ),
                            patientWithDetails = PatientWithDetailsModel(
                                patient = PatientModel(name = "Luna", breed = "Felino")
                            )
                        )
                    ),
                    lowStockProducts = listOf(
                        ProductWithDetailsModel(
                            product = ProductModel(
                                id = "p1",
                                commercialName = "Amoxicilina 500mg",
                                stock = 3,
                                minStock = 10
                            ),
                            unitType = AppCatalogModel(name = "unidades")
                        )
                    )
                ),
                onAction = {},
                onNavigationMain = {}
            )
        }
    }
}