package xyz.zpayh.myadapter.util;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Administrator on 2016/11/14.
 */

public class FrescoUtil {
    private FrescoUtil(){}

    public static void setWrapAndResizeImage(@NonNull final SimpleDraweeView view,
                                             @DrawableRes int resId, final int viewWidth){
        String path = "res://包名(实际可以是任何字符串甚至留空)/" + resId;
        setWrapAndResizeImage(view, path, viewWidth, null);
    }

    public static void setWrapAndResizeImage(@NonNull final SimpleDraweeView view,
                                             @DrawableRes int resId, final int viewWidth,@Nullable Point size){
        String path = "res://包名(实际可以是任何字符串甚至留空)/" + resId;
        setWrapAndResizeImage(view, path, viewWidth, size);
    }

    public static void setWrapAndResizeImage(@NonNull final SimpleDraweeView view, @NonNull final String path,
                                             final int viewWidth,@Nullable final Point size){
        Preconditions.checkNotNull(view);
        Preconditions.checkNotNull(path);
        ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>(){
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                if (imageInfo == null){
                    return;
                }
                final float width = imageInfo.getWidth();
                final float height = imageInfo.getHeight();
                if (width * height != 0.0f) {
                    view.setAspectRatio(width / height);
                    final int viewHeight = (int) (height * viewWidth / width);

                    if (size != null){
                        size.set(viewWidth,viewHeight);
                    }

                    setResizeImage(view,path,new ResizeOptions(viewWidth,viewHeight));
                }
            }
        };

        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(path))
                .build();

        view.setController(Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(view.getController())
                .setControllerListener(controllerListener)
                .build());
    }

    public static void setWrapImage(@NonNull final SimpleDraweeView view, @NonNull final String path){
        Preconditions.checkNotNull(view);
        Preconditions.checkNotNull(path);
        ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>(){
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                if (imageInfo == null){
                    return;
                }
                final float width = imageInfo.getWidth();
                final float height = imageInfo.getHeight();
                if (width * height != 0.0f) {
                    view.setAspectRatio(width / height);
                }
            }
        };

        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(path))
                .build();

        view.setController(Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(view.getController())
                .setControllerListener(controllerListener)
                .build());
    }

    public static void setResizeImage(@NonNull final SimpleDraweeView view, @NonNull final String path,
                                      @NonNull ResizeOptions resize){
        Preconditions.checkNotNull(view);
        Preconditions.checkNotNull(path);
        Preconditions.checkNotNull(resize);

        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(path))
                .setResizeOptions(resize)
                .build();

        view.setController(Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(view.getController())
                .setAutoPlayAnimations(true)
                .build());
    }

    public static void resizeImage(@NonNull final SimpleDraweeView view, @DrawableRes int resId,
                                      int width, int height){
        String path = "res://包名(实际可以是任何字符串甚至留空)/" + resId;
        resizeImage(view,path,width,height);
    }

    public static void resizeImage(@NonNull final SimpleDraweeView view, @NonNull final String path,
                                      final int viewWidth){
        Preconditions.checkNotNull(view);
        Preconditions.checkNotNull(path);

        ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>(){
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                if (imageInfo == null){
                    return;
                }
                final float width = imageInfo.getWidth();
                final float height = imageInfo.getHeight();
                if (width * height != 0.0f) {
                    final int viewHeight = (int) (height * viewWidth / width);
                    setResizeImage(view,path,new ResizeOptions(viewWidth,viewHeight));
                }
            }
        };

        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(path))
                .build();

        view.setController(Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(view.getController())
                .setControllerListener(controllerListener)
                .build());
    }

    public static void resizeImage(@NonNull SimpleDraweeView view, @NonNull String url, int width, int height){
        com.facebook.common.internal.Preconditions.checkNotNull(view);
        com.facebook.common.internal.Preconditions.checkNotNull(url);

        Uri uri = Uri.parse(url);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(view.getController())
                .setImageRequest(request)
                .build();

        view.setController(controller);
        view.setAspectRatio((float)width/(float)height);
    }

    public static void setImage(@NonNull SimpleDraweeView view, @Nullable String path){
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(path)
                .setTapToRetryEnabled(true)
                .setOldController(view.getController())
                .build();

        view.setController(controller);
    }

    public static void delete(@NonNull Uri uri){
        ImagePipeline pipeline = Fresco.getImagePipeline();
        pipeline.evictFromDiskCache(uri);
        pipeline.evictFromMemoryCache(uri);
    }

    public static Boolean save(@NonNull String url, @NonNull File outputFile) throws IOException {
        ImagePipelineFactory factory = Fresco.getImagePipelineFactory();
        ImagePipeline pipeline = factory.getImagePipeline();

        boolean isInCache = pipeline.isInDiskCacheSync(Uri.parse(url));

        if (isInCache){
            BinaryResource resource = factory.getMainFileCache().getResource(new SimpleCacheKey(url));
            if (resource instanceof FileBinaryResource){
                FileBinaryResource fileResource = (FileBinaryResource) resource;
                FileChannel input = new FileInputStream(fileResource.getFile()).getChannel();
                FileChannel output = new FileOutputStream(outputFile).getChannel();
                output.transferFrom(input,0,input.size());
                input.close();
                output.close();
                return true;
            }
        }
        boolean isMemoryCache = pipeline.isInBitmapMemoryCache(Uri.parse(url));
        if (!isMemoryCache){
            return false;
        }
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(url))
                .build();
        DataSource<CloseableReference<CloseableImage>> dataSource =  pipeline.fetchImageFromBitmapCache(request,null);
        if (!dataSource.isFinished()){
            return false;
        }
        CloseableReference<CloseableImage> closeableImageRef = dataSource.getResult();
        Bitmap bitmap = null;

        if (closeableImageRef != null &&
                closeableImageRef.get() instanceof CloseableBitmap) {
            bitmap = ((CloseableBitmap) closeableImageRef.get()).getUnderlyingBitmap();
        }
        if (bitmap == null){
            return false;
        }
        FileOutputStream outputStream = new FileOutputStream(outputFile);

        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        outputStream.flush();
        outputStream.close();

        return true;
    }

}
