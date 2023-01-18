import qupath.ext.stardist.StarDist2D



// Specify the model file (you will need to change this!)
var pathModel = 'D:/Denmark/Data/JuanJuan/tile/QuPathFolder/scripts/he_heavy_augment.pb'

createSelectAllObject(true);
selectAnnotations();
var stardist = StarDist2D.builder(pathModel)
        .threshold(0.5)              // Probability (detection) threshold
        //.channels('PI')            // Specify detection channel
        .normalizePercentiles(1, 99) // Percentile normalization
        .pixelSize(0.5)              // Resolution for detection
        .build()

// Run detection for the selected objects
var imageData = getCurrentImageData()
var pathObjects = getSelectedObjects()
if (pathObjects.isEmpty()) {
    Dialogs.showErrorMessage("StarDist", "Please select a parent object!")
    return
}
stardist.detectObjects(imageData, pathObjects)
//selectDetections();
//runPlugin('qupath.lib.algorithms.IntensityFeaturesPlugin', '{"pixelSizeMicrons": 2.0,  "region": "ROI",  "tileSizeMicrons": 25.0,  "channel1": true,  "doMean": true,  "doStdDev": false,  "doMinMax": false,  "doMedian": false,  "doHaralick": false,  "haralickMin": NaN,  "haralickMax": NaN,  "haralickDistance": 1,  "haralickBins": 32}');




// 1 is full resolution. You may want something more like 20 or higher for small thumbnails
downsample = 1 
//remove the findAll to get all annotations, or change the null to getPathClass("Tumor") to only export Tumor annotations
annotations = getAnnotationObjects().findAll{it.getPathClass() == null}

def imageName = GeneralTools.getNameWithoutExtension(getCurrentImageData().getServer().getMetadata().getName())
//def imageData = getCurrentImageData()
//Make sure the location you want to save the files to exists - requires a Project
def pathOutput = buildFilePath(PROJECT_BASE_DIR, 'image_export')
mkdirs(pathOutput)
def cellLabelServer = new LabeledImageServer.Builder(imageData)
    .backgroundLabel(0, ColorTools.WHITE) // Specify background label (usually 0 or 255)
    //.useCells()
    .useDetections()
    .useInstanceLabels()
    .downsample(downsample)    // Choose server resolution; this should match the resolution at which tiles are exported    
    .multichannelOutput(false) // If true, each label refers to the channel of a multichannel binary image (required for multiclass probability)
    .build()
def annotationLabelServer = new LabeledImageServer.Builder(imageData)
    .backgroundLabel(0, ColorTools.WHITE) // Specify background label (usually 0 or 255)
    .downsample(downsample)    // Choose server resolution; this should match the resolution at which tiles are exported    
    .multichannelOutput(false) // If true, each label refers to the channel of a multichannel binary image (required for multiclass probability)
    .build()


annotations.eachWithIndex{anno,x->
    roi = anno.getROI()
    def requestROI = RegionRequest.createInstance(getCurrentServer().getPath(), 1, roi)
   
    pathOutput = buildFilePath(PROJECT_BASE_DIR, 'image_export', imageName)
    //Now to export one image of each type per annotation (in the default case, unclassified
    
    //objects with overlays as seen in the Viewer    
    //writeRenderedImageRegion(getCurrentViewer(), requestROI, pathOutput+"_rendered.tif")
    //Labeled images, either cells or annotations
    //writeImageRegion(annotationLabelServer, requestROI, pathOutput+"_annotationLabels.tif")
    writeImageRegion(cellLabelServer, requestROI, pathOutput+"_maskLabels.tif")
    
    //To get the image behind the objects, you would simply use writeImageRegion
    //writeImageRegion(getCurrentServer(), requestROI, pathOutput+"_original.tif")

} 


println 'Done, image saved in '+ pathOutput