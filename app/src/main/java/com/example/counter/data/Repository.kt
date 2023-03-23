package com.example.counter.data

import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class Repository @Inject constructor(
    DataSource: DataSource,
) {
    val dataSource = DataSource
}