package com.example.theweatherapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.theweatherapp.viewmodel.WeatherViewModel
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SaveUtilsTest {

    @Mock
    lateinit var mockContext: Context

    @Mock
    lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var util: SaveUtils

    private val key = "test_key"
    private val value = "test_value"

    @Before
    fun setUp() {

        util = SaveUtils()
        // Initialize mocks
        MockitoAnnotations.openMocks(this)

        // Mock the SharedPreferences and its Editor behavior
        `when`(mockContext.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)).thenReturn(mockSharedPreferences)
        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
        `when`(mockSharedPreferences.getString(key, null)).thenReturn(value)
    }

    @Test
    fun test_save_in_sharedPreferences() {

        util.saveString(mockContext, key, value)
        assertEquals(util.getString(mockContext, key), value)
    }
}