import {requireNativeComponent, useWindowDimensions} from 'react-native';
import React, {useEffect, useRef} from 'react';
import {PixelRatio, UIManager, findNodeHandle} from 'react-native';

const TicketsViewManager = requireNativeComponent('TicketsViewManager');

const createFragment = viewId =>
  UIManager.dispatchViewManagerCommand(
    viewId,
    // we are calling the 'create' command
    UIManager.TicketsViewManager.Commands.create.toString(),
    [viewId],
  );

export const TicketsSdk = () => {
  const ref = useRef(null);
  const height = useWindowDimensions().height;
  const width = useWindowDimensions().width;

  useEffect(() => {
    const viewId = findNodeHandle(ref.current);
    createFragment(viewId);
  }, []);

  return (
    <TicketsViewManager
      style={{
        // converts dpi to px, provide desired height
        height: PixelRatio.getPixelSizeForLayoutSize(height),
        // converts dpi to px, provide desired width
        width: PixelRatio.getPixelSizeForLayoutSize(width),
      }}
      ref={ref}
    />
  );
};
