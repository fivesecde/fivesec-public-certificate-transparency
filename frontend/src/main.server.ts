import {bootstrapApplication, BootstrapContext} from '@angular/platform-browser';
import {App} from './app/app';
import {appConfig} from './app/app.config';
import {serverConfig} from './app/app.config.server';
import {mergeApplicationConfig} from '@angular/core';

const config = mergeApplicationConfig(appConfig, serverConfig);

const bootstrap = (context: BootstrapContext) =>
  bootstrapApplication(App, config, context);

export default bootstrap;
