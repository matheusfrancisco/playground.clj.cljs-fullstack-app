# Fullstack App With helix

We use the [helix](https://github.com/lilactown/helix) framework to develop our dashboard, it's a library that makes it easy to use **React** using **ClojureScript**.

## Developer environment

**how to get up the development environment?**

```bash
npm i
npm run watch
```

## Repl on your terminal

```bash
npm run repl
#inside repl

shadow.user => (shadow/watch :app)
#then open the http://localhost:8000

shadow.user =>  (shadow/repl :app)
cljs.user=> (in-ns 'api.server)
# start the server

```


### Run on REPL VIM Users

```bash
npm run watch
```
In your neovim run 

```bash
:ConjureShadowSelect app
```

Go to the `(ns api.server)` and run  to start the mock service
```clojure

(comment
  (def server (start))
  #_(.stop server)
  )

```

