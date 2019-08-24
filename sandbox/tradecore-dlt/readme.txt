StatsMain:
  - subscribes to updates published to PubSub by the lifecycle processor
  - stores timestamps and various metrics in Spanner, so that the latency of F2B flow can be evaluated
    - Use Spanner instance tradecore-stats, statsdb, stats table