import {ApplicationConfig, InjectionToken} from '@angular/core';
import {provideServerRendering, withRoutes} from '@angular/ssr';
import {serverRoutes} from './app.routes.server';

export const API_KEY_TOKEN = new InjectionToken<string>('API_KEY');

export const serverConfig: ApplicationConfig = {
  providers: [
    provideServerRendering(withRoutes(serverRoutes)),
    {
      provide: API_KEY_TOKEN,
      useFactory: () => {
        console.log(">>> Providing API_KEY:", process.env["API_KEY"]); // DEBUG
        return process.env['API_KEY'] || '';
      },
    }
  ],
};
