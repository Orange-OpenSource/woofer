# Discover logs in Kibana

This video shows you how to create discover views in Kibana to explore Java & access logs pushed by Woofer.

<iframe width="800" height="450" src="https://www.youtube.com/embed/wp6D7C3RwSY" frameborder="0" allowfullscreen></iframe>

---

## Pre-requisites: Logstash configuration

In order to be able to send directly logs from Woofer to Logstash in JSON format, you have to setup a Logstash [`tcp`
input](https://www.elastic.co/guide/en/logstash/current/plugins-inputs-tcp.html) with `json` codec as follows:

```yaml
input {
  tcp {
    port => 4321
    codec => json
  }
}
```
