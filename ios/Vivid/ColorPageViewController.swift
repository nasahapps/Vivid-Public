//
//  ColorPageViewController.swift
//  Vivid
//
//  Created by Hakeem on 12/1/15.
//  Copyright © 2015 Nasah Apps. All rights reserved.
//

import UIKit
import SafariServices
import Toast_Swift

class ColorPageViewController: UIPageViewController {

    @IBOutlet weak var favoriteBarItem: UIBarButtonItem!

    var colors = [Color]()
    var currentPosition: Int!
    var previousVc, nextVc: ColorViewController?
    let colorManager = ColorManager.shared

    override func viewDidLoad() {
        super.viewDidLoad()

        dataSource = self
        delegate = self

        setViewControllers([getColorViewController(position: currentPosition)!], direction: UIPageViewController.NavigationDirection.forward, animated: false, completion: nil)
        self.title = self.colors[self.currentPosition].colorDescription

        if (colors[currentPosition].isFavorite) {
            self.favoriteBarItem.image = UIImage(named: "star_filled")
        } else {
            self.favoriteBarItem.image = UIImage(named: "star")
        }
        
        let tap = UITapGestureRecognizer(target: self, action: #selector(toggleNavigationBarVisibility(_:)))
        self.view.addGestureRecognizer(tap)
        self.view.isUserInteractionEnabled = true

        // Show a disclaimer if first time
        if !UserDefaults.standard.wasDisclaimerShown {
            let alert = UIAlertController(title: nil, message: "Note: On-screen color representations may vary from actual paint colors", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (UIAlertAction) in
                self.navigationController?.view.makeToast("Tap the background to go full-screen", duration: 4.0, position: ToastPosition.center)
            }))
            self.present(alert, animated: true, completion: nil)
            UserDefaults.standard.wasDisclaimerShown = true
        }
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.setNavigationBarHidden(false, animated: true)
        navigationController?.hidesBarsOnSwipe = false
    }

    func getColorViewController(position: Int) -> ColorViewController? {
        if position < 0 || position >= colors.count {
            return nil
        }

        let vc = ColorViewController()
        vc.color = colors[position]
        return vc
    }

    @IBAction func toggleFavorite(_ sender: Any?) {
        let color = self.colors[self.currentPosition]
        if (color.isFavorite) {
            // Unfavorite
            colorManager.setColor(color, asFavorite: false)
            self.favoriteBarItem.image = UIImage(named: "star")
        } else {
            // Make favorite
            colorManager.setColor(color, asFavorite: true)
            self.favoriteBarItem.image = UIImage(named: "star_filled")
        }
    }

    @IBAction func performWebSearch(_ sender: Any?) {
        let color = self.colors[self.currentPosition]
        if let urlString = "\(color.brand!) \(color.colorDescription!)".addingPercentEncoding(withAllowedCharacters: .urlPathAllowed),
            let url = URL(string: "https://www.google.com/search?q=\(urlString)") {
            let vc = SFSafariViewController(url: url)
            present(vc, animated: true, completion: nil)
        }
    }
    
    @objc func toggleNavigationBarVisibility(_ sender: Any?) {
        if navigationController?.navigationBar.isHidden == true {
            navigationController?.setNavigationBarHidden(false, animated: true)
        } else {
            navigationController?.setNavigationBarHidden(true, animated: true)
        }
    }

}

extension ColorPageViewController: UIPageViewControllerDataSource, UIPageViewControllerDelegate {
    func pageViewController(_ pageViewController: UIPageViewController, didFinishAnimating finished: Bool, previousViewControllers: [UIViewController], transitionCompleted completed: Bool) {
        if completed {
            if pageViewController.viewControllers?.first == self.previousVc {
                self.currentPosition = self.currentPosition - 1
                self.nextVc = previousViewControllers.first as? ColorViewController
            } else if pageViewController.viewControllers?.first == self.nextVc {
                self.currentPosition = self.currentPosition + 1
                self.previousVc = previousViewControllers.first as? ColorViewController
            }
            
            self.title = self.colors[self.currentPosition].colorDescription
            
            if (colors[currentPosition].isFavorite) {
                self.favoriteBarItem.image = UIImage(named: "star_filled")
            } else {
                self.favoriteBarItem.image = UIImage(named: "star")
            }
        }
    }
    
    func pageViewController(_ pageViewController: UIPageViewController, viewControllerBefore viewController: UIViewController) -> UIViewController? {
        self.previousVc = getColorViewController(position: self.currentPosition - 1)
        return previousVc
    }
    
    func pageViewController(_ pageViewController: UIPageViewController, viewControllerAfter viewController: UIViewController) -> UIViewController? {
        self.nextVc = getColorViewController(position: self.currentPosition + 1)
        return nextVc
    }
}
