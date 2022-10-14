package com.conduent.nationalhighways.listener

import com.conduent.nationalhighways.ui.customviews.BottomNavigationView

interface OnNavigationItemChangeListener {
    fun onNavigationItemChanged(navigationItem: BottomNavigationView.NavigationItem)
}