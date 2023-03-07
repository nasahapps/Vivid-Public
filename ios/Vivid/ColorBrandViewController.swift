//
//  ColorBrandViewController.swift
//  Vivid
//
//  Created by Hakeem on 8/20/15.
//  Copyright (c) 2015 Nasah Apps. All rights reserved.
//

import UIKit

protocol ColorBrandViewControllerDelegate: class {
    func onBrandPicked(_ brand: String)
}

class ColorBrandViewController: UITableViewController {
    
    var currentBrand: String!
    weak var delegate: ColorBrandViewControllerDelegate?
    private let colorManager = ColorManager.shared
    var brands: [String]!

    override func viewDidLoad() {
        super.viewDidLoad()
        navigationController?.hidesBarsOnSwipe = false
        navigationController?.setNavigationBarHidden(false, animated: true)
        navigationController?.setToolbarHidden(true, animated: true)
    }

    // MARK: - Table view data source
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return brands.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "brand", for: indexPath)
        let brand = brands[indexPath.row]
        cell.textLabel?.text = brand
        if (brand == currentBrand) {
            cell.accessoryType = .checkmark
        } else {
            cell.accessoryType = .none
        }
        
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        // Pick that brand
        delegate?.onBrandPicked(brands[indexPath.row])
        self.dismiss(animated: true, completion: nil)
    }
    
}
