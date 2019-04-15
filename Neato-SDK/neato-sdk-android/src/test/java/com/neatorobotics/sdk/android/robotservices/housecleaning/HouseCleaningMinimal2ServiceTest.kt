/*
 * Copyright (c) 2019.
 * Neato Robotics Inc.
 */

package com.neatorobotics.sdk.android.robotservices.housecleaning

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

@RunWith(MockitoJUnitRunner::class)
class HouseCleaningMinimal2ServiceTest {


    lateinit var service: HouseCleaningMinimal2Service

    @Before
    fun setup() {
        service = HouseCleaningMinimal2Service()
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testEcoMode() {
        assertFalse(service.isEcoModeSupported)
    }

    @Test
    fun testTurboMode() {
        assertFalse(service.isTurboModeSupported)
    }

    @Test
    fun testModifier() {
        assertFalse(service.isCleaningFrequencySupported)
    }

    @Test
    fun testExtraCare() {
        assertTrue(service.isExtraCareModeSupported)
    }

    @Test
    fun testCleaningArea() {
        assertFalse(service.isCleaningAreaSupported)
    }
}
