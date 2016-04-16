(ns opdsp.davaccess
  (:import (org.apache.http.impl.client BasicResponseHandler HttpClients BasicCredentialsProvider HttpClientBuilder BasicAuthCache LaxRedirectStrategy CloseableHttpClient)
           (java.net URI)
           (org.apache.http.client.methods HttpRequestBase HttpGet)
           (org.apache.http.auth AuthScope UsernamePasswordCredentials)
           (org.apache.http.client.protocol HttpClientContext)
           (org.apache.http HttpHost)
           (org.apache.http.impl.auth BasicScheme)
           (org.apache.http.client ResponseHandler)
           (org.apache.http.util EntityUtils))
  (:require [clj-yaml.core :as yaml])
  )

(require ['lock-key.core :refer ['decrypt 'decrypt-as-str 'decrypt-from-base64]])


(defn basicContextFor [String host ^Integer port] (doto (HttpClientContext/create)
                                                    (.setAuthCache (doto (BasicAuthCache.)
                                                                     (.put (HttpHost. host port) (BasicScheme.))))))

(def settingsPath (clojure.string/join "/" [ (java.lang.System/getenv "HOME") ".opds-p"]))

(defn loadSettings [] (let [file (clojure.string/join "/" [settingsPath "settings.yaml"])]
                        (yaml/parse-string (slurp file))
                        ))

(def settings (atom (loadSettings)))

(defn key [] (let [settingsKey (:key (first @settings))]
           (decrypt-from-base64 settingsKey "9qPBq1kFkOfPy5w9")
           ))

(defn PROPFIND [^URI uri] (doto (proxy [HttpRequestBase] []
                             (getMethod
                               []
                               (str "PROPFIND"))) (.setURI uri)))


;CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
;credentialsProvider.setCredentials(AuthScope.ANY,
;                                    new UsernamePasswordCredentials("username", "password"));

(defn loadList [path] (
                        with-open [client (.build (doto (HttpClientBuilder/create)
                                                    ;(.setDefaultCredentialsProvider (doto (BasicCredentialsProvider.)
                                                    ;                                  (.setCredentials AuthScope/ANY (UsernamePasswordCredentials. "lkuka", "Ap7phei:x"))))
                                                    ;(.setRedirectStrategy (LaxRedirectStrategy.))
                                                    ))]
                        (let [
                              get (doto (PROPFIND (URI. "https" "webdav.yandex.ru" (str "/" path) nil))
                                    (.addHeader "Depth" "1")
                                    (.addHeader "Accept", "*/*")
                                    (.addHeader "Authorization" (str "OAuth " (key)))
                                    )
                              ]
                          (.execute client get (BasicResponseHandler.)))))

(defn loadFile [path] (
                        with-open [^CloseableHttpClient client (.build (doto (HttpClientBuilder/create)))]
                        (let [
                              url (URI. "https" "webdav.yandex.ru" (str "/" path) nil)
                              get (doto (HttpGet. url)
                                    (.addHeader "Accept", "*/*")
                                    (.addHeader "Authorization" (str "OAuth " (key)))
                                    )
                              ]
                          (.execute client get (doto (proxy [ResponseHandler] []
                                                       (handleResponse
                                                         [response]
                                                         (EntityUtils/toByteArray (.getEntity response)))))))))
