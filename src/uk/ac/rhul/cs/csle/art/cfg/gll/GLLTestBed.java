package uk.ac.rhul.cs.csle.art.cfg.gll;

public class GLLTestBed extends GLLBaseLine {
  final boolean sppfTail, mgll, lifoTasks, fifoTasks, randomTasks, popGuard, taskGuard;

  public GLLTestBed(boolean sppfTail, boolean mgll, boolean lifoTasks, boolean fifoTasks, boolean randomTasks, boolean popGuard, boolean taskGuard) {
    super();
    this.sppfTail = sppfTail;
    this.mgll = mgll;
    this.lifoTasks = lifoTasks;
    this.fifoTasks = fifoTasks;
    this.randomTasks = randomTasks;
    this.popGuard = popGuard;
    this.taskGuard = taskGuard;
  }

}
