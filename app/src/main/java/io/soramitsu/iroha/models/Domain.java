package io.soramitsu.iroha.models;


import com.google.gson.annotations.SerializedName;

/**
 * Iroha name.
 */
public class Domain extends ResponseObject {
    @SerializedName("domain")
    private String name;
    private String creator;
    private String creationDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
