package com.karoke.myoyasing.presentation.featureUI.auth.login

import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.hbb20.CountryCodePicker
import com.karoke.myoyasing.R


private val OyaBlack = Color(0xFF171419)
private val OyaDark = Color(0xFF121617)

private val OyaPurpleDark = Color(0xFF23152A)
private val OyaPurple = Color(0xFFD788FC)
private val OyaPurpleLight = Color(0xFFE5B4FF)

private val OyaText = Color(0xFFF2EFF3)
private val OyaSecondaryText = Color(0xFFB9ADB9)
private val OyaMutedText = Color(0xFF817681)

private val OyaField = Color(0xFF29232C)
private val OyaFieldBorder = Color(0xFF3B3340)

private val OyaDivider = Color(0xFF302B32)

private val OyaSocial = Color(0xFF202528)

@Composable
fun LoginScreen(
    onContinue: (countryCode: String, phoneNumber: String) -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    onClose: () -> Unit = {}
) {

    var phoneNumber by remember {
        mutableStateOf("")
    }

    var selectedCountryCode by remember {
        mutableStateOf("+1")
    }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF171419),
                        Color(0xFF24152A),
                        Color(0xFF17191B),
                        Color(0xFF121617)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .background(Color(0xFF171419))
                    .padding(start = 15.dp, end = 15.dp, top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.home_icon),
                    contentDescription = "Home Icon",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(60.dp)
                )

                Text(
                    text = "Oya",
                    color = OyaPurpleLight,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Sing",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF342C36))
            )


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(
                    modifier = Modifier.height(36.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.homemic),
                    contentDescription = "My Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .width(150.dp),

                    )

                Text(
                    text = stringResource(R.string.welcome_back),
                    color = OyaText,
                    fontFamily = FontFamily(Font(R.font.sora_bold)),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = stringResource(R.string.sign_in_to_start_your_performance),
                    color = Color(0xFFBEB5BE),
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.sora_medium)),
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(46.dp)
                )


                Text(
                    text = stringResource(R.string.mobile_number),
                    modifier = Modifier.fillMaxWidth(),
                    color = OyaMutedText,
                    fontSize = 10.sp,
                    fontFamily = FontFamily(Font(R.font.sora_medium)),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )



                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF0E0E0E),
                                    Color(0x4D4F4352)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = OyaFieldBorder,
                            shape = RoundedCornerShape(7.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AndroidView(
                        modifier = Modifier
                            .width(100.dp)
                            .fillMaxHeight()
                            .padding(start = 8.dp),
                        factory = { context ->
                            CountryCodePicker(context).apply {

                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                setContentColor(android.graphics.Color.WHITE)
                                setDefaultCountryUsingNameCode("US")
                                ccpDialogRippleEnable = true
                                setDialogBackgroundColor(getResources().getColor(R.color.white))
                                setTextSize(28)
                                showFlag(true)
                                showNameCode(false)
                                showFullName(false)


                                setFlagSize(30)



                                gravity = Gravity.CENTER


                                setOnCountryChangeListener {
                                    selectedCountryCode =
                                        selectedCountryCodeWithPlus
                                }

                                // Initialize the calling code.
                                selectedCountryCode =
                                    selectedCountryCodeWithPlus
                            }
                        }
                    )

                    // Vertical divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(Color(0xFF3B353D))
                    )


                    BasicTextField(
                        value = phoneNumber,
                        onValueChange = { input ->
                            phoneNumber = input.filter { it.isDigit() }.take(12)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        textStyle = TextStyle(
                            color = OyaText,
                            fontSize = 13.sp
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (phoneNumber.isEmpty()) {
                                    Text(
                                        text = stringResource(
                                            R.string.enter_phone_number
                                        ),
                                        color = Color(0xFF756D76),
                                        fontSize = 10.sp
                                    )
                                }

                                innerTextField()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (error != null) {
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(
                            RoundedCornerShape(7.dp)
                        )
                        .background(OyaPurpleLight)
                        .clickable(enabled = !isLoading) {
                            if (phoneNumber.length >= 13) {
                                Toast.makeText(
                                    context,
                                    "Please enter correct mobile number",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                onContinue(
                                    selectedCountryCode,
                                    phoneNumber
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = if (isLoading) "Please wait..." else "Continue",
                        color = Color(0xFF652477),
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.sora_semiblold))
                    )
                }

                Spacer(
                    modifier = Modifier.height(23.dp)
                )


                Spacer(
                    modifier = Modifier.height(15.dp)
                )


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {


                    Spacer(
                        modifier = Modifier.height(33.dp)
                    )


                    Text(
                        text = stringResource(R.string.terms_policy) +
                                "Terms of Service and Privacy Policy.",
                        color = Color(0xFF857D85),
                        fontSize = 10.sp,
                        fontFamily = FontFamily(Font(R.font.sora_regular)),
                        textAlign = TextAlign.Center
                    )


                }
            }
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true,
    widthDp = 247,
    heightDp = 480
)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        onContinue = { _, _ -> },
        onClose = {},
        isLoading = false,
        error = null
    )
}