package click.kobaken.rxirohaandroid.model;

import java.io.Serializable;
import java.util.List;

public class TransactionHistory extends BaseModel implements Serializable {
    public List<Transaction> history;
}
