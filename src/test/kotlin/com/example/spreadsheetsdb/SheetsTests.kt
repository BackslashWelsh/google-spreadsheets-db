package com.example.spreadsheetsdb

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ClearValuesRequest
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SheetsTests {
    private val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

    @BeforeEach
    fun setUp() {
        val range = "tdd"
        sheets().values().clear(SHEET_ID, range, ClearValuesRequest())
            .execute()
    }

    @Test
    fun `should clean`() {
        `should set header`()

        val range = "tdd"
        val cl = sheets().values().clear(SHEET_ID, range, ClearValuesRequest())
            .execute()
        println(cl)

        val response = sheets().values()[SHEET_ID, range]
            .execute()
        val values = response.getValues()
        assertNull(values)
    }

    @Test
    fun `should set header`() {
        val range = "tdd!A1:D1"
        val values = ValueRange().setValues(listOf(listOf("username", "email", "DOB", "password")))
        sheets().values().update(SHEET_ID, range, values).setValueInputOption("RAW").execute()

        val header = sheets().values()[SHEET_ID, range]
            .execute().getValues()

        assertTrue(header[0].size == 4)
    }

    @Test
    fun `should append users`() {
        val range = "tdd"
        val mike = listOf("Mike", "mike@email.com", "12-10-2006", "password")
        val mikeValue = ValueRange().setValues(listOf(mike))
        val jessica = listOf("Jessica", "jessica@email.com", "12-10-2006", "password")
        val jessicaValue = ValueRange().setValues(listOf(jessica))

        sheets().values().append(SHEET_ID, range, mikeValue).setValueInputOption("RAW").execute()
        sheets().values().append(SHEET_ID, range, jessicaValue).setValueInputOption("RAW").execute()

        val values = sheets().values()[SHEET_ID, range]
            .execute().getValues()
        assertTrue(values.size == 2)
        assertThat(values[0]).isEqualTo(mike)
        assertThat(values[1]).isEqualTo(jessica)
    }

//    @Test
    fun `should convert from object to request value`() {
        val user = User("Mike", "mike@email.com", "12-10-2006", "password")
        val rqst = user.sheet

        user.run {  }
        assertAll({ assertTrue(user.username == rqst[0]) },
            { assertTrue(user.email == rqst[1]) })

    }

    @Test
    fun `should append from object`() {
        val range = "tdd"
        val user = User("Mike", "mike@email.com", "12-10-2006", "password")
        val mikeValue = ValueRange().setValues(listOf(user.sheet))

        sheets().values().append(SHEET_ID, range, mikeValue).setValueInputOption("RAW").execute()

        val values = sheets().values()[SHEET_ID, range]
            .execute().getValues()

        assertEquals(values[0], user.sheet)

    }

    private fun sheets(): Sheets.Spreadsheets {
        val googleCredentials = GoogleCredentials.getApplicationDefault()
            .createScoped(SCOPES)

        val service = Sheets.Builder(httpTransport, JSON_FACTORY, HttpCredentialsAdapter(googleCredentials))
            .setApplicationName(APPLICATION_NAME)
            .build()
        return service.spreadsheets()
    }

}