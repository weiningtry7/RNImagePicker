#import <NativeImagePickerSpec/NativeImagePickerSpec.h>
#import "TZImagePickerController.h"
#import <UIKit/UIKit.h>

@interface NativeImagePicker : NSObject <NativeImagePickerSpec, TZImagePickerControllerDelegate, UINavigationControllerDelegate, UIImagePickerControllerDelegate, UIActionSheetDelegate>

@end
