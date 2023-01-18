setImageType('FLUORESCENCE');

import qupath.ext.stardist.StarDist2D



// Specify the model file (you will need to change this!)
var pathModel = 'D:/Denmark/Data/JuanJuan/tile/QuPathFolder/scripts/he_heavy_augment.pb'

createSelectAllObject(true);
selectAnnotations();
var stardist = StarDist2D.builder(pathModel)
        .threshold(0.5)              // Probability (detection) threshold
        .channels('DAPI')            // Specify detection channel
        .normalizePercentiles(1, 99) // Percentile normalization
        .pixelSize(0.5)              // Resolution for detection
        .build()
        .measureShape()
        .measureIntensity()

// Run detection for the selected objects
var imageData = getCurrentImageData()
var pathObjects = getSelectedObjects()
if (pathObjects.isEmpty()) {
    Dialogs.showErrorMessage("StarDist", "Please select a parent object!")
    return
}
stardist.detectObjects(imageData, pathObjects)



runObjectClassifier("ClassifierName");
