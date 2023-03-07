//
//  ColorService.swift
//  Vivid
//
//  Created by Hakeem on 12/6/15.
//  Copyright © 2015 Nasah Apps. All rights reserved.
//

import Foundation

class ColorUtils {

    static func getColorForHexCode(hex: String?) -> UIColor {
        if let hex = hex {
            let cleanString = hex.replacingOccurrences(of: "#", with: "", options: .literal, range: nil)
            var red, green, blue: Double
            var baseValue: UInt32 = 0x0
            Scanner(string: cleanString).scanHexInt32(&baseValue)
            red = Double((baseValue >> 16) & 0xff) / 255.0
            green = Double((baseValue >> 8) & 0xff) / 255.0
            blue = Double((baseValue >> 0) & 0xff) / 255.0

            return UIColor(red: CGFloat(red), green: CGFloat(green), blue: CGFloat(blue), alpha: 1.0)
        }

        return UIColor(red: 0.0, green: 0.0, blue: 0.0, alpha: 0.0)
    }
    
    
    
}
