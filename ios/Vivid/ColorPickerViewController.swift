//
//  ColorPickerViewController.swift
//  Vivid
//
//  Created by Hakeem on 8/19/15.
//  Copyright (c) 2015 Nasah Apps. All rights reserved.
//

import UIKit
import StoreKit

class ColorPickerViewController: UIViewController {

    @IBOutlet weak var collectionView: UICollectionView!

    var colors = [Color]()
    var brands = [String]()
    var brand: String?
    let colorManager = ColorManager.shared

    override func viewDidLoad() {
        super.viewDidLoad()
        
        collectionView.dataSource = self
        collectionView.delegate = self
        
        colorManager.getBrands { [weak self] (brands) in
            self?.brands = brands
            self?.brand = brands.first(where: { $0 == UserDefaults.standard.lastBrand }) ?? brands.first
            self?.getColorsForSelectedBrand()
        }

        if let reviewTimestamp = UserDefaults.standard.reviewTimestamp {
            // Time interval is in seconds
            // We'll check after a week, which is 604,800 seconds
            if Date().timeIntervalSince(reviewTimestamp) > 604800 {
                SKStoreReviewController.requestReview()
                UserDefaults.standard.reviewTimestamp = Date()
            }
        } else {
            UserDefaults.standard.reviewTimestamp = Date()
        }
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        // Have nav bar disappear when scrolling (enabled on this screen)
        navigationController?.hidesBarsOnSwipe = true
        navigationController?.setNavigationBarHidden(false, animated: true)
        navigationController?.setToolbarHidden(false, animated: true)
    }

    func getColorsForSelectedBrand() {
        if let brand = brand {
            self.title = brand
            colorManager.getColors(forBrand: brand) { [weak self] (colors) in
                self?.colors = colors
                self?.setupView()
            }
        }
    }

    func setupView() {
        self.collectionView.reloadData()
        // Scroll to the top of the UICollectionView
        self.collectionView.scrollToItem(at: IndexPath(row: 0, section: 0), at: .top, animated: false)
    }

    // MARK: - Navigation
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        switch segue.identifier {
        case "colorBrandView":
            let vc = segue.destination as! ColorBrandViewController
            vc.delegate = self
            vc.currentBrand = self.brand!
            vc.brands = self.brands
            if let ppc = vc.popoverPresentationController {
                ppc.delegate = self
            }
        case "colorPageView":
            let cell = sender as! UICollectionViewCell
            let index = collectionView.indexPath(for: cell)?.row ?? 0
            let vc = segue.destination as? ColorPageViewController
            vc?.colors = self.colors
            vc?.currentPosition = index
        default: break
        }
    }
    
    // When rotating, have collectionView refresh its cell sizes and num of columns
    override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        super.viewWillTransition(to: size, with: coordinator)
        collectionView.collectionViewLayout.invalidateLayout()
    }
}

extension ColorPickerViewController: UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return colors.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "color", for: indexPath as IndexPath)
        cell.backgroundColor = ColorUtils.getColorForHexCode(hex: colors[indexPath.row].hexCode!)
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        let screenWidth = collectionView.bounds.width
        let numColumns = Int(screenWidth / 100)
        let size = screenWidth / CGFloat(numColumns)
        return CGSize(width: size, height: size)
    }
    
    func collectionView(_collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        return UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 0
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 0
    }
}

extension ColorPickerViewController: ColorBrandViewControllerDelegate {
    func onBrandPicked(_ brand: String) {
        if brand != self.brand {
            self.brand = brand
            UserDefaults.standard.lastBrand = brand
            self.getColorsForSelectedBrand()
        }
    }
}

extension ColorPickerViewController: UIPopoverPresentationControllerDelegate {
    func presentationController(_ controller: UIPresentationController, viewControllerForAdaptivePresentationStyle style: UIModalPresentationStyle) -> UIViewController? {
        if let nav = controller.presentedViewController as? UINavigationController {
            addCancelButton(nav)
            return nav
        } else {
            let nav = UINavigationController(rootViewController: controller.presentedViewController)
            addCancelButton(nav)
            return nav
        }
    }
    
    func presentationController(_ presentationController: UIPresentationController, willPresentWithAdaptiveStyle style: UIModalPresentationStyle, transitionCoordinator: UIViewControllerTransitionCoordinator?) {
        if style == .none, let nav = presentationController.presentedViewController as? UINavigationController {
            removeCancelButton(nav)
        }
    }
    
    func addCancelButton(_ nav: UINavigationController) {
        let rootVc = nav.viewControllers.first
        rootVc?.navigationItem.leftBarButtonItem = UIBarButtonItem(barButtonSystemItem: .cancel, target: self, action: #selector(cancelPresentedView))
    }
    
    func removeCancelButton(_ nav: UINavigationController) {
        let rootVc = nav.viewControllers.first
        rootVc?.navigationItem.leftBarButtonItem = nil
    }
    
    @objc func cancelPresentedView() {
        presentedViewController?.dismiss(animated: true, completion: nil)
    }
}
