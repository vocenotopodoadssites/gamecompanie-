package com.example

import com.example.data.model.BusinessCatalog
import com.example.data.model.GameEra
import com.example.data.model.ProductCatalog
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testProductCatalogContains100Products() {
    assertEquals(100, ProductCatalog.BASE_PRODUCTS.size)
  }

  @Test
  fun testBusinessCatalogContains50Businesses() {
    assertEquals(50, BusinessCatalog.ALL_50_BUSINESSES.size)
  }

  @Test
  fun testGameEraProgressionOrder() {
    assertEquals(5, GameEra.values().size)
    assertEquals(GameEra.SUBSISTENCE_FARM, GameEra.values()[0])
    assertEquals(GameEra.SPACE_CORPORATION, GameEra.values()[4])
  }
}
