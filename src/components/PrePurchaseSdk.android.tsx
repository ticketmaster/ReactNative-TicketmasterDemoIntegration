import {requireNativeComponent, useWindowDimensions} from 'react-native';
import React, {useEffect, useRef} from 'react';
import {PixelRatio, UIManager, findNodeHandle} from 'react-native';
import Config from 'react-native-config';

const PrePurchaseViewManager = requireNativeComponent('PrePurchaseViewManager');

const createFragment = viewId =>
  UIManager.dispatchViewManagerCommand(
    viewId,
    // we are calling the 'create' command
    UIManager.PrePurchaseViewManager.Commands.create.toString(),
    [viewId],
  );

export const PrePurchaseSdk = () => {
  const ref = useRef(null);
  const height = useWindowDimensions().height;
  const width = useWindowDimensions().width;
  const textProps = {
    attractionId: Config.DEMO_HOST_ID,
  };

  useEffect(() => {
    const viewId = findNodeHandle(ref.current);
    createFragment(viewId);
  }, []);

  return (
    <PrePurchaseViewManager
      style={{
        // converts dpi to px, provide desired height
        height: PixelRatio.getPixelSizeForLayoutSize(height),
        // converts dpi to px, provide desired width
        width: PixelRatio.getPixelSizeForLayoutSize(width),
      }}
      textProps={textProps}
      ref={ref}
    />
  );
};
