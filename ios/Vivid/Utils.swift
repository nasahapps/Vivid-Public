//
// Created by Hakeem Hasan on 6/24/16.
// Copyright (c) 2016 Nasah Apps. All rights reserved.
//

import Foundation

extension UIViewController {
    var isIpad: Bool {
        return self.traitCollection.horizontalSizeClass == .regular && self.traitCollection.verticalSizeClass == .regular
    }
}
