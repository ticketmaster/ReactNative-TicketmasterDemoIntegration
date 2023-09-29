import React, {useCallback, useState} from 'react';
import {PrePurchaseSdk} from '../components/PrePurchaseSdk';
import {
  Platform,
  Pressable,
  SafeAreaView,
  SectionList,
  StatusBar,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import ChevronRight from '../assets/svg/ChevronRight';
import {useFocusEffect} from '@react-navigation/native';
import Config from 'react-native-config';

const PrePurchase = () => {
  const [initialFocus, setInitialFocus] = useState(false);
  const [showPrePurchaseSdk, setShowPrePurchaseSdk] = useState(false);

  useFocusEffect(
    useCallback(() => {
      if (Platform.OS === 'ios') {
        // Show SDK's when screen focuses
        setInitialFocus(false);
        // unmount the RN components after the Native SDK's are launched,
        // so that the SDK can be reinitialized via RN's button onPress after the SDK's Native close button is pressed
        setTimeout(() => {
          setInitialFocus(false);
        }, 500);
        console.log('PrePurchase useFocusEffect mount called');
        return () => {
          setInitialFocus(false);
          console.log('PrePurchase useFocusEffect unmount called');
        };
      }
    }, []),
  );

  const onShowPrePurchaseSdk = () => {
    setShowPrePurchaseSdk(true);
    setTimeout(() => {
      setShowPrePurchaseSdk(false);
    }, 500);
  };

  const DATA = [
    {
      header: 'PrePurchase SDK',
      data: [
        {
          title: 'PrePurchase SDK',
          platforms: ['ios'],
          onPress: () => onShowPrePurchaseSdk(),
          first: true,
        },
      ],
    },
  ];

  return (
    <SafeAreaView>
      {Platform.OS === 'ios' ? (
        <>
          <SectionList
            sections={DATA}
            keyExtractor={(item, index) => item.title + index}
            renderItem={({item}) => (
              <>
                {item.platforms.includes(Platform.OS) && (
                  <>
                    <Pressable
                      onPress={() => item.onPress && item.onPress()}
                      style={({pressed}) => [
                        styles.item,
                        item.first && styles.topItem,
                        item.last && styles.bottomItem,
                        {
                          backgroundColor: pressed ? '#00000008' : 'white',
                        },
                      ]}>
                      <Text style={styles.title}>{item.title}</Text>
                      <View>
                        <ChevronRight />
                      </View>
                    </Pressable>

                    {!item.last && <View style={styles.horizontalLine} />}
                  </>
                )}
              </>
            )}
            renderSectionHeader={({section: {header, data}}) => (
              <>
                {data.some(item => item.platforms.includes(Platform.OS)) && (
                  <View style={styles.headerWrapper}>
                    <Text style={styles.header}>{header}</Text>
                  </View>
                )}
              </>
            )}
          />
          {(initialFocus || showPrePurchaseSdk) && (
            // Wrapper the SDK modal view in an additional view prevents the main view from being unresponsive after the SDK is presented
            <View>
              <PrePurchaseSdk attractionIdProp={Config.DEMO_ATTRACTION_ID} />
            </View>
          )}
        </>
      ) : (
        <PrePurchaseSdk />
      )}
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: StatusBar.currentHeight,
    marginHorizontal: 16,
  },
  item: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#ffffff',
    padding: 12,
  },
  topItem: {
    borderTopLeftRadius: 16,
    borderTopRightRadius: 16,
  },
  bottomItem: {
    borderBottomLeftRadius: 16,
    borderBottomRightRadius: 16,
  },
  header: {
    color: '#808080',
    fontSize: 16,
  },
  headerWrapper: {
    marginLeft: 20,
    marginTop: 24,
    marginBottom: 8,
  },
  title: {
    fontSize: 18,
  },
  horizontalLine: {
    backgroundColor: '#bfbfbf',
    height: 0.5,
    marginLeft: 20,
  },
});

export default PrePurchase;
