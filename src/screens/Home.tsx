import React, {useEffect, useState} from 'react';
import {
  Platform,
  Pressable,
  SectionList,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import {NativeModules} from 'react-native';
import ChevronRight from '../assets/svg/ChevronRight';
import {TicketsSdk} from '../components/TicketsSdk.ios';
import Config from 'react-native-config';

const Home = () => {
  const {AccountsSDK} = NativeModules;
  const [showTicketsSdk, setShowTicketsSdk] = useState(false);

  useEffect(() => {
    onConfigureAccountsSDK();
  }, []);

  const DATA = [
    {
      header: 'Accounts SDK',
      data: [
        {
          title: 'Login',
          platforms: ['ios', 'android'],
          onPress: () => onLogin(),
          first: true,
        },
        {
          title: 'Logout',
          platforms: ['ios', 'android'],
          onPress: () => onLogout(),
        },
        {
          title: 'IsLoggedIn',
          platforms: ['ios', 'android'],
          onPress: () => isLoggedIn(),
        },
        {
          title: 'Refresh Token',
          platforms: ['ios', 'android'],
          onPress: () => onRefreshToken(),
        },
        {
          title: 'Get Member Info',
          platforms: ['ios', 'android'],
          onPress: () => getMemberInfo(),
        },
        {
          title: 'Get Token',
          platforms: ['ios', 'android'],
          onPress: () => getToken(),
        },
        {
          title: 'Show Retail PrePurchase Venue',
          platforms: ['android', 'ios'],
          onPress: () => {
            AccountsSDK.presentPrePurchaseVenue(Config.DEMO_VENUE_ID);
          },
        },
        {
          title: 'Show Retail PrePurchase Attraction',
          platforms: ['android', 'ios'],
          onPress: () => {
            AccountsSDK.presentPrePurchaseAttraction(Config.DEMO_ATTRACTION_ID);
          },
        },
        {
          title: 'Show Retail Purchase',
          platforms: ['android', 'ios'],
          onPress: () => {
            AccountsSDK.presentPurchase(Config.DEMO_EVENT_ID);
          },
          last: true,
        },
      ],
    },
    {
      header: 'Tickets SDK',
      data: [
        {
          title: 'Tickets SDK (Modal)',
          platforms: ['ios'],
          onPress: () => onShowTicketsSDK(),
          first: true,
          last: true,
        },
      ],
    },
  ];

  const onShowTicketsSDK = () => {
    setShowTicketsSdk(true);
    setTimeout(() => {
      setShowTicketsSdk(false);
    }, 500);
  };

  const onConfigureAccountsSDK = async () => {
    try {
      const result = await AccountsSDK.configureAccountsSDK();
      console.log('Configuration set: ', result);
    } catch (e: any) {
      console.log('Accounts SDK Configuration error:', (e as Error).message);
    }
  };

  const onLogin = async () => {
    try {
      if (Platform.OS === 'android') {
        AccountsSDK.login((resultCode: any) => {
          console.log('Login result code: ', resultCode);
        });
      } else if (Platform.OS === 'ios') {
        const result = await AccountsSDK.login();
        console.log('Accounts SDK Login access token:', result);
      }
    } catch (err) {
      console.log('Accounts SDK Login error:', (err as Error).message);
    }
  };

  const onLogout = async () => {
    try {
      await AccountsSDK.logout();
      console.log('User logged out');
    } catch (e: any) {
      console.log('Could not log out: ', (e as Error).message);
    }
  };

  const isLoggedIn = async () => {
    try {
      if (Platform.OS === 'android') {
        const result = await AccountsSDK.isLoggedIn();
        console.log('Is logged in: ', result);
      } else if (Platform.OS === 'ios') {
        const result = await AccountsSDK.isLoggedIn();
        console.log('Is logged in: ', result.result);
      }
    } catch (e: any) {
      !(e as Error).message.includes('User not logged in') &&
        console.log('IsLoggedIn error: ', (e as Error).message);
    }
  };

  const onRefreshToken = async () => {
    try {
      const result = await AccountsSDK.refreshToken();
      console.log('Accounts SDK access token: ', result);
    } catch (e: any) {
      console.log('Account SDK Refresh Token error: ', (e as Error).message);
    }
  };

  const getMemberInfo = async () => {
    try {
      const result = await AccountsSDK.getMemberInfo();
      // Android result is a JSON String not an Object
      console.log('Member Info: ', result);
    } catch (e: any) {
      console.log('Member Info error: ', (e as Error).message);
    }
  };

  const getToken = async () => {
    try {
      if (Platform.OS === 'android') {
        const result = await AccountsSDK.refreshToken();
        console.log('Accounts SDK Login access token:', result);
      } else if (Platform.OS === 'ios') {
        // iOS getToken has the exact same Native logic as refreshToken, but will not display the login UI if a user is not logged in
        const result = await AccountsSDK.getToken();
        console.log('Accounts SDK Login access token:', result);
      }
    } catch (e: any) {
      !(e as Error).message.includes('User not logged in') &&
        console.log('Get Token error: ', (e as Error).message);
    }
  };

  return (
    <>
      <SectionList
        sections={DATA}
        stickySectionHeadersEnabled={false}
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
        contentContainerStyle={styles.listPadding}
      />
      {showTicketsSdk && (
        <View>
          <TicketsSdk />
        </View>
      )}
    </>
  );
};

const styles = StyleSheet.create({
  item: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#ffffff',
    padding: 12,
    marginHorizontal: 12,
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
    fontSize: 20,
  },
  headerWrapper: {
    marginLeft: 20,
    marginTop: 14,
    marginBottom: 8,
  },
  listPadding: {
    paddingBottom: 60,
  },
  title: {
    fontSize: 16,
    color: 'black',
  },
  horizontalLine: {
    height: 2,
  },
});

export default Home;
