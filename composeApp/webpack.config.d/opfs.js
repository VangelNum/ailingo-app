config.devServer = {
  ...config.devServer,
  headers: {
    "Cross-Origin-Embedder-Policy": "credentialless",
    "Cross-Origin-Opener-Policy": "same-origin",
  },
//  https: true
}