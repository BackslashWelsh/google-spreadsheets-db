package com.example.spreadsheetsdb

import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.SheetsScopes

const val APPLICATION_NAME = "Google Sheets API Java Quickstart"
val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
val SCOPES = listOf(SheetsScopes.SPREADSHEETS)
val SHEET_ID = requireNotNull(System.getenv("SHEET_ID"))