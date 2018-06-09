/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common

import java.io.File

import scala.collection.JavaConversions.mapAsJavaMap
import scala.collection.mutable.Buffer
import org.scalatest.Matchers
import TestUtils._

import scala.collection.mutable

trait CommandLine extends Matchers {

  /**
   * The base command to run. This returns a new mutable buffer, intended for building the rest of the command line.
   */
  def baseCommand: Buffer[String]

  /**
   * Runs a command wsk [params] where the arguments come in as a sequence.
   *
   * @return RunResult which contains stdout, stderr, exit code
   */
  def cli(params: Seq[String],
          expectedExitCode: Int = SUCCESS_EXIT,
          verbose: Boolean = false,
          env: Map[String, String] = Map("WSK_CONFIG_FILE" -> ""),
          workingDir: File = new File("."),
          stdinFile: Option[File] = None,
          showCmd: Boolean = false,
          hideFromOutput: Seq[String] = Seq(),
          retriesOnNetworkError: Int = 3): RunResult = {
    val args = baseCommand
    if (verbose) args += "--verbose"
    if (showCmd) println(args.mkString(" ") + " " + params.mkString(" "))
    val rr = retry(
      0,
      retriesOnNetworkError,
      () =>
        TestUtils.runCmd(
          DONTCARE_EXIT,
          workingDir,
          TestUtils.logger,
          sys.env ++ env,
          stdinFile.getOrElse(null),
          args ++ params: _*))

    withClue(hideStr(reportFailure(args ++ params, expectedExitCode, rr).toString(), hideFromOutput)) {
      if (expectedExitCode != TestUtils.DONTCARE_EXIT) {
        val ok = (rr.exitCode == expectedExitCode) || (expectedExitCode == TestUtils.ANY_ERROR_EXIT && rr.exitCode != 0)
        if (!ok) {
          rr.exitCode shouldBe expectedExitCode
        }
      }
    }

    rr
  }

  /** Retries cmd on network error exit. */
  private def retry(i: Int, N: Int, cmd: () => RunResult): RunResult = {
    val rr = cmd()
    if (rr.exitCode == NETWORK_ERROR_EXIT && i < N) {
      Thread.sleep(1.second.toMillis)
      println(s"command will retry to due to network error: $rr")
      retry(i + 1, N, cmd)
    } else rr

  /**
   * Takes a string and a list of sensitive strings. Any sensistive string found in
   * the target string will be replaced with "XXXXX", returning the processed string.
   */
  private def hideStr(str: String, hideThese: Seq[String]): String = {
    // Iterate through each string to hide, replacing it in the target string (str)
    hideThese.fold(str)((updatedStr, replaceThis) => updatedStr.replace(replaceThis, "XXXXX"))
  }

  private def reportFailure(args: Buffer[String], ec: Integer, rr: RunResult) = {
    val s = new StringBuilder()
    s.append(args.mkString(" ") + "\n")
    if (rr.stdout.nonEmpty) s.append(rr.stdout + "\n")
    if (rr.stderr.nonEmpty) s.append(rr.stderr)
    s.append("exit code:")
  }
}

object WskAdmin {
  val wskadmin = new CommandLine {
    override def baseCommand: mutable.Buffer[String] = WskAdmin.baseCommand
  }

  private val binDir = WhiskProperties.getFileRelativeToWhiskHome("bin")
  private val binaryName = "wskadmin"

  def exists = {
    val dir = binDir
    val exec = new File(dir, binaryName)
    assert(dir.exists, s"did not find $dir")
    assert(exec.exists, s"did not find $exec")
  }

  def baseCommand = {
    Buffer(WhiskProperties.python, new File(binDir, binaryName).toString)
  }

  def listKeys(namespace: String, pick: Integer = 1): List[(String, String)] = {
    wskadmin
      .cli(Seq("user", "list", namespace, "--pick", pick.toString))
      .stdout
      .split("\n")
      .map("""\s+""".r.split(_))
      .map(parts => (parts(0), parts(1)))
      .toList
  }
}