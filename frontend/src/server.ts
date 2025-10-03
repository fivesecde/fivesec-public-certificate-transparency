import {
  AngularNodeAppEngine,
  createNodeRequestHandler,
  isMainModule,
  writeResponseToNodeResponse,
} from '@angular/ssr/node';
import express from 'express';
import {join} from 'node:path';
import {createProxyMiddleware} from 'http-proxy-middleware';

const browserDistFolder = join(import.meta.dirname, '../browser');
const app = express();
const angularApp = new AngularNodeAppEngine();

app.use('/api', (req, _res, next) => {
  req.headers['x-api-key'] = process.env['API_KEY'] || '';
  req.headers['accept'] = 'application/json';
  next();
});

app.use(
  '/api',
  createProxyMiddleware({
    target: process.env['BACKEND_URL'],
    changeOrigin: true,
  }),
);

app.use(
  express.static(browserDistFolder, {
    maxAge: '1y',
    index: false,
    redirect: false,
  }),
);

app.use((req, res, next) => {
  angularApp
    .handle(req)
    .then((response) =>
      response ? writeResponseToNodeResponse(response, res) : next(),
    )
    .catch(next);
});

if (isMainModule(import.meta.url)) {
  const port = process.env['PORT'] || 4000;
  app.listen(port, (error) => {
    if (error) throw error;
    console.log(`Node Express server listening on http://localhost:${port}`);
    console.log(
      `Proxying /api → ${process.env['BACKEND_URL'] || 'http://192.168.178.200:8881'}`
    );
  });
}

export const reqHandler = createNodeRequestHandler(app);
