//
//  PreferencesService.swift
//  Vivid
//
//  Created by Hakeem on 8/19/15.
//  Copyright (c) 2015 Nasah Apps. All rights reserved.
//

import Foundation

private let KEY_LAST_BRAND = "lastBrand"
private let KEY_DISCLAIMER_SHOWN = "disclaimerShown"
private let KEY_COLOR_DB_VERSION = "colorDbVersion"
private let KEY_REVIEW_TIMESTAMP = "reviewTimestamp"

extension UserDefaults {
    var lastBrand: String? {
        get {
            return string(forKey: KEY_LAST_BRAND)
        }
        set {
            set(newValue, forKey: KEY_LAST_BRAND)
        }
    }
    
    var wasDisclaimerShown: Bool {
        get {
            return bool(forKey: KEY_DISCLAIMER_SHOWN)
        }
        set {
            set(newValue, forKey: KEY_DISCLAIMER_SHOWN)
        }
    }
    
    var reviewTimestamp: Date? {
        get {
            return object(forKey: KEY_REVIEW_TIMESTAMP) as? Date
        }
        set {
            set(newValue, forKey: KEY_REVIEW_TIMESTAMP)
        }
    }
}
