/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools # Templates
 * and open the template in the editor.
 */
package org.kiwi.utils;

import java.util.List;
import java.util.Locale;

/**
 *
 * @author i2165aq
 */
public abstract class BenchExecutor extends Thread {

    public abstract boolean execute();

    boolean terminated = false;
    int threadId;
    long loop;
    long min = Integer.MAX_VALUE;
    long max = 0;
    long avg = -1;
    double tps = -1;
    long total = 0;

    long success = 0;
    long error = 0;

    public BenchExecutor(int threadId, int loop) {
        this.loop = loop;
        this.threadId = threadId;
        //do first call to init all context
        execute();
    }

    @Override
    public void run() {
        //execute bench
        _execute();
    }

    public void _execute() {

        for (int i = 0; i < loop; i++) {
            long start = System.currentTimeMillis();

            try {
                if (execute()) {
                    success++;
                } else {
                    error++;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            long end = System.currentTimeMillis();
            long time = end - start;
            if (min > time) {
                min = time;
            }
            if (max < time) {
                max = time;
            }
            total += time;
        }
        terminated = true;
        if (total > 0) {
            avg = total / loop;
            tps = (double) 1000 / (total / loop);
        }
        System.out.println(getPrintableResult());
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public long getTotal() {
        return total;
    }

    public long getAVG() {
        return avg;
    }

    public double getTPS() {
        return tps;
    }

    public long getLoop() {
        return loop;
    }

    public long getSuccess() {
        return success;
    }

    public long getError() {
        return error;
    }

    public void shutdown() {
    }

    public static String getPrintableHeader() {
        return String.format(
                "---------------------------------------------------------------------------------------------%n"
                + "# %10s # %8s # %8s # %8s # %8s # %10s # %8s # %8s #%n"
                + "---------------------------------------------------------------------------------------------",
                "ThreadId", "TPS", "AVG (ms)", "Min (ms)", "Max (ms)", "Total (ms)", "Success", "Error");
    }

    public String getPrintableResult() {
        //use local US to have dot separator for decimal value
        return String.format(Locale.US, "# %10s # %8.2f # %8s # %8s # %8s # %10s # %8s # %8s #",
                threadId, getTPS(), getAVG(), getMin(), getMax(), getTotal(), success, error);
    }

    public static String getPrintableTotal(List<BenchExecutor> executors, long executionTime) {
        long _loop = 0;
        long _min = Integer.MAX_VALUE;
        long _max = 0;
//        long _total = 0;
        long _avg = 0;
        double _tps = 0;
        long _success = 0;
        long _error = 0;
        for (BenchExecutor ex : executors) {
            _loop += ex.getLoop();
//            _total += ex.getTotal();
            _success += ex.getSuccess();
            _avg += ex.getAVG();
            _error += ex.getError();
            if (_min > ex.getMin()) {
                _min = ex.getMin();
            }
            if (_max < ex.getMax()) {
                _max = ex.getMax();
            }
        }

        if (_success != 0) {
            _tps = (double) 1000 / (executionTime / _success);
            _avg = _avg / _success;
        }

        //use local US to have dot separator for decimal value
        return String.format(Locale.US,
                "---------------------------------------------------------------------------------------------%n"
                + "# %10s # %8.2f # %8s # %8s # %8s # %10s # %8s # %8s #%n"
                + "---------------------------------------------------------------------------------------------%n%n"
                + " Execution time : %s %n"
                + " Total TPS : %.2f %n"
                + " Total SUCCESS : %s %n"
                + " Total ERROR : %s %n",
                "*", _tps, _avg, _min, _max, executionTime, _success, _error,
                executionTime, _tps, _success, _error);
    }
}
