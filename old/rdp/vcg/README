README FOR THE VCG TOOL (Windows 3.1)
=====================================

HARDWARE REQUIREMENTS 
---------------------

DX 386, 486 or Pentium, i.e. with working Floating Point Unit.
Memory: at least 8 MB, otherwise it is too slow.
Disk space: about 7 MB.
Color Graphics Adapter, recommended: 1024x768 with 16M colors
or better.
It is possible to work with 640x400 or 800x600, but some 
dialog boxes are rather large and thus, bad usable.
It is possible to work with B&W or 256 colors, but the demo is
designed for 16M colors and will look a little bit ugly.


FOR NON-UNIX-ER
---------------
If you only use MS DOS computers, you will probably be surprised 
by some explanations in the documentation, help files, or in
the demo, which come from the UNIX world. Sorry for this.
Please note that the same docs/help files/demo is also used in
the UNIX version of the VCG tool. I have not the time to do this
fine tuning adaption of the docs/help files/demo  i.e. removing all
explanations that are related to UNIX by explanations related to
MSDOS.


FOR MSDOS/MSWINDOWS
-------------------
Please note that this is an alpha release. There might be bugs in 
this software. The origin of bugs is mostly that integers are only 16 bit
thus that we have overflows during multiplications. On the UNIX systems,
integers are 32 bit, thus overflow is no problem there.
It is very difficult for me to check all arithmetic operations for 
overflows, because the program sources are about 2.5 MB. I guess I have
already found the most errors of this type, but you can't be sure.


HOW TO INSTALL THE VCG TOOL
---------------------------
Sorry, but there is no automatical install script available.
You have to do the following:

Unpack the VCG directory tree at any place you wish:

   pkunzip -d vcg_p1.zip
   pkunzip -d vcg_p2.zip

The VCG directory tree looks like this:

   VCG\
      BIN\     <- the main executable, the help files and the 
                  demonstration program
      EXPL\    <- the example specifications
      FONTS\   <- the VCG fonts
      ICONS\   <- the ICONS of the examples
      MAN\     <- the UNIX manual pages
      DOC\     <- the documentation (PostScript)

Now set the following environment variables:

   VCGTOOL    <- access path to the VCG\BIN\VCG.EXE
   VCGFONTS   <- access path to the VCG fonts
   VCGICONS   <- access path to the VCG icons

This can be added to AUTOEXEC.BAT.
For instance, if the directory VCG is in the root directory,
add the following lines to AUTOEXEC.BAT:

   set VCGTOOL=\VCG\BIN\VCG.EXE
   set VCGFONTS=\VCG\FONTS
   set VCGICONS=\VCG\ICONS

Don't forget to restart the computer after the changes of AUTOEXEC.BAT.

Optionally, you can add the executables VCG\BIN\VCG.EXE and
VCG\DEMO\VCGDEMO.EXE to some program group. Use the windows
program manager as usually to do this.

Now, it's time to start the demo:
From DOS, type   WIN VCG\DEMO\VCGDEMO.EXE   (or whatever the path is)
or from Windows, click onto the icon of the VCGDEMO.EXE.

Have fun !




DRAWBACKS (COMPARED TO THE UNIX VERSION)
----------------------------------------

Animation works, but is currently not documented and completely different
to the UNIX version. We use DDE.
Icon sizes are restricted to 10000 pixel color or 30000 pixel monochrome.
All X11 specific options (-geometry, -font, -display, ...) don't work. 
Graph sizes are restricted to 32767 nodes and 32767 edges. 
Memory is allocated in blocks of 64 K, which is much slower than in the
UNIX version. Well, it is difficult to compare.
The VCG tool does not react on signals.
The VCG tool does not touch files as answer on animation.
Transition animation does not work with fisheyes. The result with fisheyes
is ugly.
Transition animation speed depends on the speed of the video graphics 
adapter and may be too slow (or to fast ?)
The batch mode works, but it requires that Windows is running.



ACKNOWLEDMENTS
--------------
The MS Windows version of VCG was implemented using the free portable library
wxWindows. Thanks to Julian Smart for providing this library, and thanks to
the 'wxWinners' for many useful extensions of the library.
Iris Lemke has implemented the first version of the VCG tool on Sunview.
All this work here is based on her work.
For more, see the manual page or the help file.


