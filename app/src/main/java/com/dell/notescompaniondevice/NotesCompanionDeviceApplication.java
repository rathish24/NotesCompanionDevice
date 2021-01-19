// Copyright @ MyScript. All rights reserved.

package com.dell.notescompaniondevice;

import android.app.Application;

import com.dell.notescompaniondevice.utils.certificate.MyCertificate;
import com.myscript.iink.Engine;

public class NotesCompanionDeviceApplication extends Application
{
  private static Engine engine;

  public static synchronized Engine getEngine()
  {
    if (engine == null)
    {
      engine = Engine.create(MyCertificate.getBytes());
    }

    return engine;
  }

}
