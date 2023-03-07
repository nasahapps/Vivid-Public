//
//  ColorViewController.swift
//  Vivid
//
//  Created by Hakeem on 8/25/15.
//  Copyright (c) 2015 Nasah Apps. All rights reserved.
//

import UIKit

class ColorViewController: UIViewController {
    
    var color: Color!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.backgroundColor = ColorUtils.getColorForHexCode(hex: color.hexCode!)
    }
    
}
