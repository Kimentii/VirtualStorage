package com.kimentii.virtualstorage

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class GetAimNextPositionTest {

    @Test
    fun whenAimCanBeDirectlyMoved() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val mapSource = ArrayList<String>()
        mapSource.add("-----")
        mapSource.add("-   -")
        mapSource.add("E BR-")
        mapSource.add("-   -")
        mapSource.add("-----")
        val mapMatrix = mapSource.map { s -> s.toCharArray() }
        val map = Map(mapMatrix.toTypedArray())
        val robot = Robot(appContext, map, 1, CommandsFactory.getAllCommands())
        robot.setNewLocation(3, 2)
        robot.setAim(2, 2)

        val result = Cell(1, 2)
        assertEquals(robot.aimNextPosition, result)
    }

    @Test
    fun whenAimCantBeDirectlyMovedItShouldBeMovedDown() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val mapSource = ArrayList<String>()
        mapSource.add("-----")
        mapSource.add("-   -")
        mapSource.add("E-BR-")
        mapSource.add("-   -")
        mapSource.add("-----")
        val mapMatrix = mapSource.map { s -> s.toCharArray() }
        val map = Map(mapMatrix.toTypedArray())
        val robot = Robot(appContext, map, 1, CommandsFactory.getAllCommands())
        robot.setNewLocation(3, 2)
        robot.setAim(2, 2)

        val result = Cell(2, 3)
        assertEquals(robot.aimNextPosition, result)
    }

    @Test
    fun whenAimCantBeMovedDownItShouldBeMovedToTop() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val mapSource = ArrayList<String>()
        mapSource.add("-----")
        mapSource.add("- - -")
        mapSource.add("E-BR-")
        mapSource.add("-   -")
        mapSource.add("-----")
        val mapMatrix = mapSource.map { s -> s.toCharArray() }
        val map = Map(mapMatrix.toTypedArray())
        val robot = Robot(appContext, map, 1, CommandsFactory.getAllCommands())
        robot.setNewLocation(3, 2)
        robot.setAim(2, 2)

        val result = Cell(2, 3)
        //    Log.d("Test", "(" + robot.aimNextPosition.x + ", " + robot.aimNextPosition.y + ")")
        assertEquals(robot.aimNextPosition, result)
    }

    @Test
    fun ifThereIsNowWayThenNull() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val mapSource = ArrayList<String>()
        mapSource.add("-----")
        mapSource.add("- - -")
        mapSource.add("E-BR-")
        mapSource.add("- - -")
        mapSource.add("-----")
        val mapMatrix = mapSource.map { s -> s.toCharArray() }
        val map = Map(mapMatrix.toTypedArray())
        val robot = Robot(appContext, map, 1, CommandsFactory.getAllCommands())
        robot.setNewLocation(3, 2)
        robot.setAim(2, 2)

        val result = null
//        Log.d("Test", "(" + robot.aimNextPosition.x + ", " + robot.aimNextPosition.y + ")")
        assertNull(robot.aimNextPosition)
    }
}
