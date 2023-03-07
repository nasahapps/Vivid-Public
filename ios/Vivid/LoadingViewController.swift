//
//  ViewController.swift
//  Vivid
//
//  Created by Hakeem on 8/17/15.
//  Copyright (c) 2015 Nasah Apps. All rights reserved.
//

import UIKit

class LoadingViewController: UIViewController {

    @IBOutlet weak var loadingLabel: UILabel!
    @IBOutlet weak var progressView: UIProgressView!

    override func viewDidLoad() {
        super.viewDidLoad()
        // Load brands
        initializeApp()
    }

    func initializeApp() {
        ColorManager.shared.loadDataIfNecessary(onProgress: { [weak self] (current, max) in
            self?.updateProgressBar(progress: current, max: max)
        }) { [weak self] (result) in
            if case .success = result {
                self?.performSegue(withIdentifier: "colorPickerView", sender: nil)
            } else if case let .failure(error) = result {
                let alert = UIAlertController(title: "Failed to start app", message: error.localizedDescription, preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action) in
                    exit(0)
                }))
                self?.present(alert, animated: true, completion: nil)
            }
        }
    }

    private func updateProgressBar(progress: Int, max: Int) {
        progressView.isHidden = false
        loadingLabel.isHidden = false

        let percentage = Float(progress / max)
        progressView.setProgress(percentage, animated: true)
    }

}

