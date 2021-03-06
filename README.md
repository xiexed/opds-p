# Opds-p

An [OPDS](http://opds-spec.org/) server that proxies OPDS calls to yandex-disk webdav.

## Configuring

Put to `$HOME/.opds-p/app-settings.yaml` the following configuration:

```yaml
yandex-app:
  id: "<id-of-registered-yandex-application>"
  password: "<password-of-registered-yandex-application>"
```


## Usage

**Start server**

    lein ring server

or

    lein ring server-headless

**Build war**

    lein ring uberwar opds-p.war
    
## Development with REPL

```clojure
(require '[opdsp.core :refer :all])
(require '[ring.adapter.jetty :as rj]) (require '[opdsp.core :refer :all])
(future (rj/run-jetty standalone-routes {:port 3000}))
```