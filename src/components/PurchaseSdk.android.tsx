import {requireNativeComponent, useWindowDimensions} from 'react-native';
import React, {useEffect, useRef} from 'react';
import {PixelRatio, UIManager, findNodeHandle} from 'react-native';
import Config from 'react-native-config';

const PurchaseViewManager = requireNativeComponent('PurchaseViewManager');

const createFragment = viewId =>
  UIManager.dispatchViewManagerCommand(
    viewId,
    // we are calling the 'create' command
    UIManager.PurchaseViewManager.Commands.create.toString(),
    [viewId],
  );

export const PurchaseSdk = () => {
  const ref = useRef(null);
  const height = useWindowDimensions().height;
  const width = useWindowDimensions().width;
  const textProps = {
    eventId: Config.DEMO_EVENT_ID,
  };

  useEffect(() => {
    const viewId = findNodeHandle(ref.current);
    createFragment(viewId);
  }, []);

  return (
    <PurchaseViewManager
      style={{
        // converts dpi to px, provide desired height
        height: PixelRatio.getPixelSizeForLayoutSize(height),
        width: PixelRatio.getPixelSizeForLayoutSize(width),
      }}
      textProps={textProps}
      ref={ref}
    />
  );
};
