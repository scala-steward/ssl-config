/*
 * Copyright (C) 2015 Typesafe Inc. <http://www.typesafe.com>
 */

package com.typesafe.sslconfig.ssl

import com.typesafe.sslconfig.util.NoopLogger
import org.specs2.mutable._
import java.security.Security

object SystemPropertiesSpec extends Specification with After {

  sequential

  def after = sp.clearProperties()

  val mkLogger = NoopLogger.factory()
  val sp = new SystemConfiguration(mkLogger)

  "SystemProperties" should {

    "disableCheckRevocation should not be set normally" in {
      val config = SSLConfig(checkRevocation = None)

      val originalOscp = Security.getProperty("ocsp.enable")

      sp.configure(config)

      // http://stackoverflow.com/a/8507905/5266
      Security.getProperty("ocsp.enable") must_== originalOscp
      System.getProperty("com.sun.security.enableCRLDP") must beNull
      System.getProperty("com.sun.net.ssl.checkRevocation") must beNull
    }

    "disableCheckRevocation is set explicitly" in {
      val config = SSLConfig(checkRevocation = Some(true))

      sp.configure(config)

      // http://stackoverflow.com/a/8507905/5266
      Security.getProperty("ocsp.enable") must be("true")
      System.getProperty("com.sun.security.enableCRLDP") must be("true")
      System.getProperty("com.sun.net.ssl.checkRevocation") must be("true")
    }

    // @see http://www.oracle.com/technetwork/java/javase/documentation/tlsreadme2-176330.html
    "allowLegacyHelloMessages is not set" in {
      val config = SSLConfig(loose = SSLLooseConfig(allowLegacyHelloMessages = None))

      sp.configure(config)

      System.getProperty("sun.security.ssl.allowLegacyHelloMessages") must beNull
    }

    // @see http://www.oracle.com/technetwork/java/javase/documentation/tlsreadme2-176330.html
    "allowLegacyHelloMessages is set" in {
      val config = SSLConfig(loose = SSLLooseConfig(allowLegacyHelloMessages = Some(true)))

      sp.configure(config)

      System.getProperty("sun.security.ssl.allowLegacyHelloMessages") must be("true")
    }

    // @see http://www.oracle.com/technetwork/java/javase/documentation/tlsreadme2-176330.html
    "allowUnsafeRenegotiation not set" in {
      val config = SSLConfig(loose = SSLLooseConfig(allowUnsafeRenegotiation = None))

      sp.configure(config)

      System.getProperty("sun.security.ssl.allowUnsafeRenegotiation") must beNull
    }

    // @see http://www.oracle.com/technetwork/java/javase/documentation/tlsreadme2-176330.html
    "allowUnsafeRenegotiation is set" in {
      val config = SSLConfig(loose = SSLLooseConfig(allowUnsafeRenegotiation = Some(true)))

      sp.configure(config)

      System.getProperty("sun.security.ssl.allowUnsafeRenegotiation") must be("true")
    }

  }

}
