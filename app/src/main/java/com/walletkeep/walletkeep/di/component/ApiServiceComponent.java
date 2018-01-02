package com.walletkeep.walletkeep.di.component;

import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.di.module.ApiServiceModule;
import com.walletkeep.walletkeep.di.scope.AppScope;

import dagger.Component;

@AppScope
@Component(modules = ApiServiceModule.class)
public interface ApiServiceComponent {

    ApiService getApiService();
}