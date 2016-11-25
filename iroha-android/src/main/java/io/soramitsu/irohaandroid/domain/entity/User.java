package io.soramitsu.irohaandroid.domain.entity;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    public String uuid;
    public String name;
    public List<Asset> assets;
}
