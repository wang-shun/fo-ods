#Upstream Load Generator**

### `UpstreamLoadGen`

This is the main class that wires up all services. You can just run this class to generate 1000 trades of Cash-Equities and put them into TRADES Bigtable as default.
However it can accept following inputs during execution (if all 5 are provided together)-
1. projectId - This is GCP ProjectId
2. instanceId - This is BigTable InstanceId
3. assetClass - Shortcode for AssetClass and Product Combination
4. tableName - Name of the BigTable
5. tCount - Total No. of Trades to be generated.

Exact steps to be used to compile and run after cloning..
`cd fo-ods-ulg`
`mvn clean install -Dmaven.test.skip=true`
`java -jar target/fo-ods-ulg-0.1.0-SNAPSHOT.jar fo-ods equitytest cash-eq TRADES 1000`

Now let's understand what each of these services do..

### `TradeGeneratorService`
`serve` method can be called to generate the number of trades for the given asset class, if the mapping file for the given asset class / product combination is present in the `resources` folder.
Soon the location of these files would be made configurable so that these files can be read from Google file storage.
`serve` takes in only the number of trades to be generated as input but the constructor of `TradeGeneratorService` requires the short code for the asset class and product to be specified.
Please refer to `TradeGeneratorServiceTest` for actual example.
The template of the trade needs to be specified in the form of a JSON file. Various supported datatypes are as follows.
1. String : There are three variations in String. 
  * value - fixed String and would be repeated with the same value for all trades
  * values - Random string would be picked from the comma separated list of Strings provided
  * format - String can have variables. e.g. EYC-${S,2}-{N,3} would mean String would always start with 'EYC-', followed by 2 random characters and a -, followed by a random three digit number.
2. int : Each time a random integer would be selected between the minRange and maxRange. If you need a fixed number then you can specify the min and max range as the same number or use type as String with value as the fixed number.
3. float : Same as Int but just that it would be a float/double with two decimal points.
4. date : Date would be today's date in the specified format.
5. timestamp : Current Time Stamp
6. uuid : Universnally Unique Alpha-Numeric String generated using RadomUUID utility.
7. sequence : Unique long number generated using converted UUID to a long number
8. Operations - multiply and add are supported currently. Use * or + to indicate the operation.
Please refer to `cash-eq-trade.json` for actual examples.

### `TradeConverterService`
`serve` method can be called to convert the number of trades for the given asset class, if the mapping file for the given asset class / product combination is present in the `resources` folder.
Soon the location of these files would be made configurable so that these files can be read from Google file storage.
`serve` takes in the list of trades to be converted into data model format as input but the constructor of `TradeConverterService` requires the short code for the asset class and product to be specified.
Please refer to `TradeConverterServiceTest` for actual example.
The template for conversion process needs to be specified in the JSON format and would contain the linkage between the fields we get from upstream and the columnfamily and qualifier from the datamodel.
The values can be specified in following ways -
   * straight-pull : The value would be pulled from that of the attribute `name` of the incoming trade data.
   * fixed : The value would be pulled from the attribute `value` given in the converter template json.
3. sequence : Unique long number generated using converted UUID to a long number
4. timestamp : Current Time Stamp
Please refer to `cash-eq-converter.json` for actual examples.

### `TradePersistenceService`
`serve` method can be called to persist the number of trades into database.
It takes in the list of trades to be inserted into data base as input but the constructor of `TradePersistenceService` requires the GCP instanceID, projectId and tableName to be specified.
Please refer to `TradePersistenceServiceTest` for actual example.