package com.mosalab.submissionpaai.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mosalab.submissionpaai.R

@Composable
fun LandingScreen(
    navController: NavController,
    modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,

        ) {
        Image(
            painter = painterResource(id = R.drawable.image_welcome),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "${stringResource(id = R.string.title_welcome_page)} Story!",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(stringResource(R.string.message_welcome_page),
            fontSize = 18.sp,
            fontWeight = FontWeight.W400
       )
        Row(

            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    navController.navigate(
                        "login"
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {

                Text(text = "Login")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    navController.navigate(
                        "register"
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text(text = "Sign Up")
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun LandingScreenPreview() {
    val navController = rememberNavController()
    LandingScreen(navController)
}
