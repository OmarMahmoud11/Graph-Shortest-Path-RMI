package org.example.GSPClient;

import java.util.List;

public class Result {
    public String clientID; // changed from int to String
    public List<String> reqs;
    public List<Integer> res;

    public Result(String clientID, List<String> reqs, List<Integer> res) {
        this.clientID = clientID;
        this.reqs = reqs;
        this.res = res;
    }
}
