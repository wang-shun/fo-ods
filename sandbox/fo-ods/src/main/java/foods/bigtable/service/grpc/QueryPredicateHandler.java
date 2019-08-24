package foods.bigtable.service.grpc;

import foods.bigtable.repository.query.predicate.Predicate;
import foods.bigtable.repository.query.predicate.PropertyPredicate;
import foods.traderepository.proto.CompareOpType;
import foods.traderepository.proto.QueryPredicate;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class QueryPredicateHandler {

  private static Logger log = LoggerFactory.getLogger(QueryPredicateHandler.class);
  private final Function<String, byte[]> serializer;
  private byte[] family;
  private byte[] qualifier;

  public QueryPredicateHandler(byte[] family, byte[] qualifier, Function<String, byte[]> serializer) {
    this.family = family;
    this.qualifier = qualifier;
    this.serializer = serializer;
  }

  public Predicate createPredicate(QueryPredicate proto) {
    CompareFilter.CompareOp compareOp = parseCompareOp(proto.getCompareOp());
    byte[] value = serializer.apply(proto.getValue());
    Predicate result = new PropertyPredicate(family, qualifier, compareOp, value, proto.getPredicateType().name(), proto.getValue());
    return result;
  }

  private CompareFilter.CompareOp parseCompareOp(CompareOpType compareOp) {
    CompareFilter.CompareOp result = CompareFilter.CompareOp.valueOf(compareOp.name());
    return result;
  }

}
