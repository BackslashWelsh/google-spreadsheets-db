package com.example.spreadsheetsdb

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ClearValuesRequest
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class SheetsKoTests : ShouldSpec({
    beforeTest {
        val range = "tdd"
        make().clear(SHEET_ID, range, ClearValuesRequest()).execute()
    }

    should("convert from object to request value") {
        val user = User("Mike", "mike@email.com", "12-10-2006", "password")
        val rqst = user.sheet

        assertSoftly(user) {
            rqst[0] shouldBe username
            rqst[1] shouldBe email
            rqst[2] shouldBe dob
            rqst[3] shouldBe password
        }
    }

    should("append from user") {
        val range = "tdd"
        val user = User("Mike", "mike@email.com", "12-10-2006", "password")
        val userValue = ValueRange().setValues(listOf(user.sheet))

        make().append(SHEET_ID, range, userValue).setValueInputOption("RAW").execute()

        val values = make()[SHEET_ID, range]
            .execute().getValues()
        values.size shouldBe 1
        values[0] shouldBe user.sheet
    }

    should("map to list of users from response") {
        val range = "tdd"

        val user = User("Mike", "mike@email.com", "12-10-2006", "password")
        val userValue = ValueRange().setValues(listOf(user.sheet))
        make().append(SHEET_ID, range, userValue).setValueInputOption("RAW").execute()

        val get = makeGet(range)

        toUsers(get)[0] shouldBe user
    }
})

fun toUsers(resp: MutableList<MutableList<Any>>): List<User> = resp
    .map { it.filterIsInstance<String>() }
    .map { User(it[0], it[1], it[2], it[3]) }

fun makeGet(range: String): MutableList<MutableList<Any>> = make()[SHEET_ID, range].execute().getValues()


private fun make(): Sheets.Spreadsheets.Values {
    val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

    val googleCredentials = GoogleCredentials.getApplicationDefault()
        .createScoped(SCOPES)

    val service = Sheets.Builder(httpTransport, JSON_FACTORY, HttpCredentialsAdapter(googleCredentials))
        .setApplicationName(APPLICATION_NAME)
        .build()
    return service.spreadsheets().values()
}
