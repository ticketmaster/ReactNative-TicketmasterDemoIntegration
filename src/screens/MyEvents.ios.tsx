import React, {useEffect, useState} from 'react';
import TicketsSdkEmbedded from '../components/TicketsSdkEmbedded';

const MyEvents = () => {
  const [initialFocus, setInitialFocus] = useState(true);

  useEffect(() => {
    // Initially, the altered Bottom Tabs View frame height is not available to Native code on iOS, this becomes available after a rerender.
    // If needed an additional delay can be used before setting to false
    setInitialFocus(false);
  }, []);

  return <>{!initialFocus && <TicketsSdkEmbedded style={{flex: 1}} />}</>;
};

export default MyEvents;
